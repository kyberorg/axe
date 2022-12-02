package pm.axe.api.user;

import kong.unirest.HttpStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pm.axe.Endpoint;
import pm.axe.api.middleware.TokenCheckerMiddleware;
import pm.axe.constants.HttpCode;
import pm.axe.db.models.User;
import pm.axe.exception.error.AxeErrorBuilder;
import pm.axe.json.AxeErrorJson;
import pm.axe.result.OperationResult;
import pm.axe.services.user.UserOperationsService;
import pm.axe.services.user.UserService;
import pm.axe.utils.ApiUtils;
import pm.axe.utils.ErrorUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DeleteUserRestController {
    public static final String TAG = "[" + DeleteUserRestController.class.getSimpleName() + "]";
    private final UserService userService;
    private final UserOperationsService userOpsService;
    private final ErrorUtils errorUtils;
    private final TokenCheckerMiddleware tokenChecker;

    /**
     * Delete User Endpoint.
     *
     * @param username string with username to delete.
     * @param force force flag. Flag's value doesn't matter, it even can be absent.
     * @param request {@link HttpServletRequest} to get headers from.
     * @return {@link ResponseEntity} with 401 - if token check failed, 404 - if username not found,
     *  403 - deletion is not permitted,  500 - server-side error.
     */
    @DeleteMapping(value = Endpoint.Api.DELETE_USER_API)
    public ResponseEntity<?> deleteUser(final @PathVariable("username") String username,
                                        final @RequestParam(required = false) String force,
                                        final HttpServletRequest request) {
        log.debug("{} got DELETE request. Username: {}, Force: {}", TAG, username, force);
        //token check
        OperationResult tokenCheck = tokenChecker.checkMasterToken(request);
        if (tokenCheck.notOk()) {
            return ApiUtils.handleTokenFail(tokenCheck);
        }

        //username check
        if (!userService.isUserExists(username)) {
            return ResponseEntity.notFound().build();
        }
        //getting user
        Optional<User> optionalUser = userService.getUserByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();
        //check on default user
        if (Objects.equals(user.getId(), userService.getDefaultUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(AxeErrorJson.createWithMessage("Self-destruction is not allowed.")
                            .andStatus(HttpCode.FORBIDDEN));
        }

        boolean forceFlag = force != null;
        OperationResult deletionResult = userOpsService.deleteUser(user, forceFlag);
        if (deletionResult.ok()) {
            return ResponseEntity.noContent().build();
        } else if (Objects.equals(deletionResult.getResult(), OperationResult.BANNED)) {
            return ResponseEntity.status(HttpCode.FORBIDDEN)
                    .body(AxeErrorJson.createWithMessage(deletionResult.getMessage()).andStatus(403));
        } else {
            log.error("{} User deletion failed. OpResult: {}", TAG, deletionResult);
            errorUtils.reportToBugsnag(AxeErrorBuilder.withTechMessage("User Deletion failed").build());
            return ApiUtils.handleServerError();
        }
    }
}

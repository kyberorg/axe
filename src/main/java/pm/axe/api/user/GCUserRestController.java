package pm.axe.api.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pm.axe.Endpoint;
import pm.axe.api.middleware.TokenCheckerMiddleware;
import pm.axe.constants.HttpCode;
import pm.axe.json.AxeErrorJson;
import pm.axe.result.OperationResult;
import pm.axe.users.GarbageUserCollector;
import pm.axe.utils.ApiUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
public class GCUserRestController {
    private static final String TAG = "[" + GCUserRestController.class.getSimpleName() + "]";
    private final GarbageUserCollector usersGC;

    private final TokenCheckerMiddleware tokenChecker;

    @PostMapping(value = Endpoint.Api.GC_USER_API)
    public ResponseEntity<?> requestGarbageUsersCollection(final HttpServletRequest request) {
        log.info("{} Got Users GC request.", TAG);
        //token check
        OperationResult tokenCheck = tokenChecker.checkMasterToken(request);
        if (tokenCheck.notOk()) {
            return ApiUtils.handleTokenFail(tokenCheck);
        }
        //check mutex
        if (usersGC.getMutex().availablePermits() == 0) {
            log.warn("{} Hit Mutex: another GC operation in progress.", TAG);
            return anotherOperationInProgress();
        }
        //triggering gc
        try {
            usersGC.collectAndDelete();
            return ResponseEntity.accepted().build();
        } catch (InterruptedException e) {
            //should not happen - but who knows.
            return anotherOperationInProgress();
        }
    }

    private ResponseEntity<AxeErrorJson> anotherOperationInProgress() {
        return ResponseEntity.status(HttpCode.CONFLICT)
                .body(AxeErrorJson.createWithMessage("Another operation in progress. Try again later."));
    }
}

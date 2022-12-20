package pm.axe.api.user;

import kong.unirest.HttpStatus;
import kong.unirest.MimeTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pm.axe.Endpoint;
import pm.axe.api.middleware.TokenCheckerMiddleware;
import pm.axe.exception.error.AxeErrorBuilder;
import pm.axe.internal.RegisterUserInput;
import pm.axe.json.AxeErrorJson;
import pm.axe.json.PostUserRequest;
import pm.axe.json.PostUserResponse;
import pm.axe.result.OperationResult;
import pm.axe.services.mail.MailService;
import pm.axe.services.user.AccountService;
import pm.axe.services.user.UserOperationsService;
import pm.axe.services.user.UserService;
import pm.axe.users.AccountType;
import pm.axe.users.PasswordValidator;
import pm.axe.users.UsernameGenerator;
import pm.axe.users.UsernameValidator;
import pm.axe.utils.ApiUtils;
import pm.axe.utils.ErrorUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * User Registration Endpoint.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class PostUserRestController {
    public static final String TAG = "[" + PostUserRestController.class.getSimpleName() + "]";

    private final MailService mailService;
    private final AccountService accountService;
    private final UserService userService;
    private final UsernameGenerator usernameGenerator;
    private final UserOperationsService userOpsService;

    private final ErrorUtils errorUtils;
    private final TokenCheckerMiddleware tokenChecker;

    /**
     * API that performs User Registration.
     *
     * @param requestJson {@link PostUserRequest} JSON with link to save
     * @param request     raw {@link HttpServletRequest} to get Headers from
     * @return {@link ResponseEntity} with {@link PostUserResponse} or {@link AxeErrorJson}.
     */
    @PostMapping(value = Endpoint.Api.REGISTER_USER_API,
            consumes = MimeTypes.JSON,
            produces = MimeTypes.JSON)
    public ResponseEntity<?> registerUser(final @RequestBody PostUserRequest requestJson,
                                          final HttpServletRequest request) {
        log.debug("{} got POST request: {}", TAG, requestJson);
        //Currently, this API is not public (at least until API Rate Limits will be implemented).
        //So it works only with Master Token provided.
        OperationResult tokenCheckResult = tokenChecker.checkMasterToken(request);
        if (tokenCheckResult.notOk()) {
            log.warn("{} Master Token Check failed - returning {}", TAG, HttpStatus.UNAUTHORIZED);
            return ApiUtils.handleTokenFail(tokenCheckResult);
        } else {
            log.info("{} Master Token Check - passed", TAG);
        }

        //checking Body
        if (requestJson == null) {
            return ApiUtils.handleError(HttpStatus.BAD_REQUEST, "Body should be a JSON object");
        }
        OperationResult inputValidationResult = requestJson.isValid();
        if (inputValidationResult.notOk()) {
            return ApiUtils.handleError(HttpStatus.UNPROCESSABLE_ENTITY, inputValidationResult.getMessage());
        }

        //extract fields
        String email = requestJson.getEmail();
        String username = requestJson.getUsername();
        String password = requestJson.getPassword();
        boolean tfaEnabled = requestJson.isTfaEnabled();

        //inputs check
        ResponseEntity<AxeErrorJson> result;

        if (StringUtils.isNotBlank(email)) {
            result = checkEmail(email);
            if (result != null) {
                return result;
            }
        }

        if (StringUtils.isNotBlank(username)) {
            result = checkUsername(username);
            if (result != null) {
                return result;
            }
        } else {
            log.info("{} There is no user-defined Username in Request. Generating custom Username", TAG);
            OperationResult usernameGenerationResult = usernameGenerator.generate();
            if (usernameGenerationResult.ok()) {
                username = usernameGenerationResult.getStringPayload();
            } else {
                log.error("{} Username generation failed. OpResult: {}", TAG, usernameGenerationResult);
                return ApiUtils.handleServerError();
            }
        }

        //password not valid -> 422
        result = checkPassword(password);
        if (result != null) {
            return result;
        }

        //Registering user
        RegisterUserInput registerUserInput = new RegisterUserInput(email, username, password, tfaEnabled);
        OperationResult userRegistrationResult = userOpsService.registerUser(registerUserInput);
        if (userRegistrationResult.notOk()) {
            log.error("{} Failed to register user. OpResult: {}", TAG, userRegistrationResult);
            errorUtils.reportToBugsnag(AxeErrorBuilder
                    .withTechMessage(userRegistrationResult.getMessage())
                    .withMessageToUser("User registration failed")
                    .build());
            return ApiUtils.handleServerError();
        }
        log.info("{} Success. User Registered - returning {}", TAG, HttpStatus.CREATED);
        PostUserResponse.Builder response = PostUserResponse.create().addEmail(email);

        if (userRegistrationResult.hasPayload(UserOperationsService.TELEGRAM_TOKEN_KEY)) {
            response.addTelegramToken(userRegistrationResult.
                    getStringPayload(UserOperationsService.TELEGRAM_TOKEN_KEY));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response.build());
    }

    private ResponseEntity<AxeErrorJson> checkEmail(final String email) {
        //email not valid -> 422 (?)
        OperationResult emailValidationResult = mailService.isEmailValid(email);
        if (emailValidationResult.notOk()) {
            return ApiUtils.handleError(HttpStatus.UNPROCESSABLE_ENTITY, emailValidationResult);
        }
        //email exists - > 409
        boolean emailAlreadyExists = accountService.isAccountAlreadyExists(email, AccountType.EMAIL);
        if (emailAlreadyExists) {
            String errMessage = String.format("Email %s is already taken. Please try another one", email);
            return ApiUtils.handleError(HttpStatus.CONFLICT, errMessage);
        }
        return null;
    }

    private ResponseEntity<AxeErrorJson> checkUsername(final String username) {
        //username not valid -> 422
        OperationResult usernameVerificationResult = UsernameValidator.isValid(username);
        if (usernameVerificationResult.notOk()) {
            return ApiUtils.handleError(HttpStatus.UNPROCESSABLE_ENTITY, usernameVerificationResult);
        }
        //username exists -> 409
        boolean isUserAlreadyExist = userService.isUserExists(username);
        if (isUserAlreadyExist) {
            return ApiUtils.handleError(HttpStatus.CONFLICT, "Username already exists. Please try another one");
        }
        return null;
    }

    private ResponseEntity<AxeErrorJson> checkPassword(final String password) {
        OperationResult passwordValidationResult = PasswordValidator.isPasswordValid(password);
        if (passwordValidationResult.notOk()) {
            return ApiUtils.handleError(HttpStatus.UNPROCESSABLE_ENTITY, passwordValidationResult);
        }
        return null;
    }
}

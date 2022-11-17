package io.kyberorg.yalsee.api.user;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.exception.error.YalseeErrorBuilder;
import io.kyberorg.yalsee.internal.RegisterUserInput;
import io.kyberorg.yalsee.json.PostUserRequest;
import io.kyberorg.yalsee.json.PostUserResponse;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.mail.MailService;
import io.kyberorg.yalsee.services.user.AccountService;
import io.kyberorg.yalsee.services.user.UserOperationsService;
import io.kyberorg.yalsee.services.user.UserService;
import io.kyberorg.yalsee.users.AccountType;
import io.kyberorg.yalsee.users.PasswordValidator;
import io.kyberorg.yalsee.users.UsernameGenerator;
import io.kyberorg.yalsee.users.UsernameValidator;
import io.kyberorg.yalsee.utils.ApiUtils;
import io.kyberorg.yalsee.utils.ErrorUtils;
import io.kyberorg.yalsee.utils.TokenChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    private final TokenChecker tokenChecker;

    /**
     * API that performs User Registration.
     *
     * @param requestJson {@link PostUserRequest} JSON with link to save
     * @param request     raw {@link HttpServletRequest} to get Headers from
     * @return {@link ResponseEntity} with {@link PostUserResponse} or {@link YalseeErrorJson}.
     */
    @PostMapping(value = Endpoint.Api.REGISTER_USER_API,
            consumes = MimeType.APPLICATION_JSON,
            produces = MimeType.APPLICATION_JSON)
    public ResponseEntity<?> registerUser(final @RequestBody PostUserRequest requestJson,
                                          final HttpServletRequest request) {
        log.debug("{} got POST request: {}", TAG, requestJson);
        //Currently, this API is not public (at least until API Rate Limits will be implemented).
        //So it works only with Master Token provided.
        OperationResult tokenCheckResult = tokenChecker.checkMasterToken(request);
        if (tokenCheckResult.notOk()) {
            log.warn("{} Master Token Check failed - returning {}", TAG, HttpCode.UNAUTHORIZED);
            return ApiUtils.handleTokenFail(tokenCheckResult);
        } else {
            log.info("{} Master Token Check - passed", TAG);
        }

        //checking Body
        if (requestJson == null) {
            return ApiUtils.handleError(HttpCode.BAD_REQUEST, "Body should be a JSON object");
        }

        //extract fields
        String email = requestJson.getEmail();
        String username = requestJson.getUsername();
        String password = requestJson.getPassword();
        boolean tfaEnabled = requestJson.isTfaEnabled();

        //inputs check
        ResponseEntity<YalseeErrorJson> result;
        result = checkEmail(email);
        if (result != null) {
            return result;
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
            errorUtils.reportToBugsnag(YalseeErrorBuilder
                    .withTechMessage(userRegistrationResult.getMessage())
                    .withMessageToUser("User registration failed")
                    .build());
            return ApiUtils.handleServerError();
        }
        log.info("{} Success. User Registered - returning {}", TAG, HttpCode.CREATED);
        PostUserResponse.Builder response = PostUserResponse.create().addEmail(email);

        if (userRegistrationResult.hasPayload(UserOperationsService.TELEGRAM_TOKEN_KEY)) {
            response.addTelegramToken(userRegistrationResult.
                    getStringPayload(UserOperationsService.TELEGRAM_TOKEN_KEY));
        }
        return ResponseEntity.status(HttpCode.CREATED).body(response.build());
    }

    private ResponseEntity<YalseeErrorJson> checkEmail(final String email) {
        //email not valid -> 422 (?)
        OperationResult emailValidationResult = mailService.isEmailValid(email);
        if (emailValidationResult.notOk()) {
            return ApiUtils.handleError(HttpCode.UNPROCESSABLE_ENTRY, emailValidationResult);
        }
        //email exists - > 409
        boolean emailAlreadyExists = accountService.isAccountAlreadyExists(email, AccountType.EMAIL);
        if (emailAlreadyExists) {
            String errMessage = String.format("Email %s is already taken. Please try another one", email);
            return ApiUtils.handleError(HttpCode.CONFLICT, errMessage);
        }
        return null;
    }

    private ResponseEntity<YalseeErrorJson> checkUsername(final String username) {
        //username not valid -> 422
        OperationResult usernameVerificationResult = UsernameValidator.isValid(username);
        if (usernameVerificationResult.notOk()) {
            return ApiUtils.handleError(HttpCode.UNPROCESSABLE_ENTRY, usernameVerificationResult);
        }
        //username exists -> 409
        boolean isUserAlreadyExist = userService.isUserExists(username);
        if (isUserAlreadyExist) {
            return ApiUtils.handleError(HttpCode.CONFLICT, "Username already exists. Please try another one");
        }
        return null;
    }

    private ResponseEntity<YalseeErrorJson> checkPassword(final String password) {
        OperationResult passwordValidationResult = PasswordValidator.isPasswordValid(password);
        if (passwordValidationResult.notOk()) {
            return ApiUtils.handleError(HttpCode.UNPROCESSABLE_ENTRY, passwordValidationResult);
        }
        return null;
    }
}
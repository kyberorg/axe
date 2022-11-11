package io.kyberorg.yalsee.api.user;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.internal.RegisterUserInput;
import io.kyberorg.yalsee.json.UserRegistrationRequest;
import io.kyberorg.yalsee.json.UserRegistrationResponse;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.mail.EmailService;
import io.kyberorg.yalsee.services.user.AccountService;
import io.kyberorg.yalsee.services.user.UserOperationsService;
import io.kyberorg.yalsee.services.user.UserService;
import io.kyberorg.yalsee.users.AccountType;
import io.kyberorg.yalsee.users.PasswordValidator;
import io.kyberorg.yalsee.users.UsernameGenerator;
import io.kyberorg.yalsee.users.UsernameValidator;
import io.kyberorg.yalsee.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * User Registration Endpoint.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class PutUserRestController {
    public static final String TAG = "[" + PutUserRestController.class.getSimpleName() + "]";

    private final EmailService emailService;
    private final AccountService accountService;
    private final UserService userService;
    private final UsernameGenerator usernameGenerator;
    private final UserOperationsService userOpsService;

    /**
     * API that performs user registration.
     *
     * @param requestJson {@link UserRegistrationRequest} JSON with link to save
     * @return {@link ResponseEntity} with {@link UserRegistrationResponse} or {@link YalseeErrorJson}.
     */
    @PutMapping(value = Endpoint.Api.USER_REGISTRATION_API,
            consumes = MimeType.APPLICATION_JSON,
            produces = MimeType.APPLICATION_JSON)
    public ResponseEntity<?> registerUser(final @RequestBody UserRegistrationRequest requestJson) {
        log.debug("{} got PUT request: {}", TAG, requestJson);
        ResponseEntity<YalseeErrorJson> result;

        if (requestJson == null) {
            return ApiUtils.handleError(HttpCode.BAD_REQUEST, "Body should be a JSON object");
        }

        String email = requestJson.getEmail();
        String username = requestJson.getUsername();
        String password = requestJson.getPassword();
        boolean tfaEnabled = requestJson.isTfaEnabled();

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

        //Register user
        RegisterUserInput registerUserInput = new RegisterUserInput(email, username, password, tfaEnabled);
        OperationResult userRegistrationResult = userOpsService.registerUser(registerUserInput);
        if (userRegistrationResult.notOk()) {
            //TODO analyze it
            log.error("{} failed to register user. OpResult: {}", TAG, userRegistrationResult);
            return ApiUtils.handleServerError();
        }
        return ResponseEntity.status(HttpCode.CREATED).body(UserRegistrationResponse.create(email));
    }

    private ResponseEntity<YalseeErrorJson> checkEmail(final String email) {
        //email not valid -> 422 (?)
        OperationResult emailValidationResult = emailService.isEmailValid(email);
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

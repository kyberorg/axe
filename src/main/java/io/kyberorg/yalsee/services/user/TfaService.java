package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.models.Authorization;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.user.verificationsenders.VerificationSenders;
import io.kyberorg.yalsee.users.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TfaService {
    private static final String ERR_TFA_DISABLED = "User disabled 2fa verification";
    private static final String ERR_NO_TFA_CHANNEL = "User has no 2fa channel";
    private static final String ERR_TFA_CHANNEL_NOT_FOUND = "Selected 2fa channel not found";
    private static final String ERR_TFA_CHANNEL_UNCONFIRMED = "Selected 2fa channel unconfirmed";
    private static final String ERR_TFA_CHANNEL_UNKNOWN = "Unable to get 2fa channel from database";
    private static final String ERR_CODE_GENERATION_FAIL = "Unable to generate 2fa code";

    private final UserPreferencesService userPreferencesService;
    private final AuthService authService;
    private final TokenService tokenService;
    private final VerificationSenders verificationSenders;

    public OperationResult sendVerificationCode(User user) {
        boolean isTfaEnabled = userPreferencesService.isTfaEnabled(user);
        if (!isTfaEnabled) {
            return OperationResult.generalFail().withMessage(ERR_TFA_DISABLED);
        }
        AuthProvider tfaChannel = userPreferencesService.getTfaChannel(user);
        if (tfaChannel == null) {
            return OperationResult.generalFail().withMessage(ERR_NO_TFA_CHANNEL);
        }

        Optional<Authorization> authorization = authService.getAuthorization(user, tfaChannel);
        if (authorization.isEmpty()) {
            return OperationResult.generalFail().withMessage(ERR_TFA_CHANNEL_NOT_FOUND);
        }
        if (!authorization.get().isConfirmed()) {
            return OperationResult.generalFail().withMessage(ERR_TFA_CHANNEL_UNCONFIRMED);
        }
        Optional<String> destination = authService.decryptAuthorizationUser(authorization.get());
        if (destination.isEmpty()) {
            return OperationResult.generalFail().withMessage(ERR_TFA_CHANNEL_UNKNOWN);
        }

        OperationResult getCodeResult = tokenService.createVerificationCode(user);
        if (getCodeResult.notOk()) {
            return OperationResult.generalFail().withMessage(ERR_CODE_GENERATION_FAIL);
        }
        String verificationCode = getCodeResult.getPayload(Token.class).getToken();

        return verificationSenders.get(tfaChannel)
                .sendVerification(destination.get(), verificationCode);
    }
}

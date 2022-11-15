package io.kyberorg.yalsee.mail.letters;

import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.services.mail.LetterType;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Data needed for creating Login Verification Letter aka OTP.
 */
@RequiredArgsConstructor
public class LoginVerificationLetter implements Letter {
    private final Token token;

    @Override
    public LetterType getLetterType() {
        return LetterType.LOGIN_VERIFICATION;
    }

    @Override
    public String getSubject() {
        return "Verification Code";
    }

    @Override
    public String getLink() {
        return "";
    }

    @Override
    public Map<String, Object> getTemplateVars() {
        Map<String, Object> vars = new HashMap<>(1);
        vars.put("code", token.getToken());
        return vars;
    }
}

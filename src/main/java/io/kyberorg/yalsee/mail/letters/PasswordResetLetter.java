package io.kyberorg.yalsee.mail.letters;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.services.mail.LetterType;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class PasswordResetLetter implements Letter {

    private final Token token;

    @Override
    public LetterType getLetterType() {
        return LetterType.PASSWORD_RESET;
    }

    @Override
    public String getSubject() {
        return "Password Reset Request";
    }

    @Override
    public String getLink() {
        return MessageFormat.format("{0}/{1}?token={2}",
                AppUtils.getShortUrlFromStaticContext(), Endpoint.UI.PASSWORD_RESET_PAGE, token.getToken());
    }

    @Override
    public Map<String, Object> getTemplateVars() {
        Map<String, Object> vars = new HashMap<>(2);
        vars.put("link", getLink());
        vars.put("username", getUsername(token));
        return vars;
    }
}

package pm.axe.mail.letters;

import lombok.RequiredArgsConstructor;
import pm.axe.Endpoint;
import pm.axe.db.models.Token;
import pm.axe.mail.LetterType;
import pm.axe.utils.AppUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Data needed to create Password Reset Letter.
 */
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

package pm.axe.mail.letters;

import pm.axe.Endpoint;
import pm.axe.mail.LetterType;
import pm.axe.db.models.Token;
import pm.axe.utils.AppUtils;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Data for creating Account Confirmation Letter.
 */
@RequiredArgsConstructor
public class AccountConfirmationLetter implements Letter {
    private final Token token;

    @Override
    public LetterType getLetterType() {
        return LetterType.ACCOUNT_CONFIRMATION;
    }

    @Override
    public String getSubject() {
        return "Account Confirmation";
    }

    @Override
    public String getLink() {
        return MessageFormat.format("{0}/{1}?token={2}",
                AppUtils.getShortUrlFromStaticContext(), Endpoint.UI.CONFIRMATION_PAGE, token.getToken());
    }

    @Override
    public Map<String, Object> getTemplateVars() {
        Map<String, Object> vars = new HashMap<>(2);
        vars.put("username", getUsername(token));
        vars.put("link", getLink());
        return vars;
    }

}

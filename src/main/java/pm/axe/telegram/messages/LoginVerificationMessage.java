package pm.axe.telegram.messages;

import lombok.RequiredArgsConstructor;
import pm.axe.db.models.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * Message with OneTime Password aka OTP, used to confirm login attempt.
 */
@RequiredArgsConstructor
public class LoginVerificationMessage implements TelegramMessage {
    private final Token token;

    @Override
    public String getTemplate() {
        return "otp.ftl";
    }

    @Override
    public Map<String, Object> getTemplateVars() throws IllegalArgumentException {
        Map<String, Object> vars = new HashMap<>(1);
        vars.put("code", token.getToken());
        return vars;
    }
}

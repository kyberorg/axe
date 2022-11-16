package io.kyberorg.yalsee.telegram.messages;

import io.kyberorg.yalsee.models.Token;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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

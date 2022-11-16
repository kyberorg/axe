package io.kyberorg.yalsee.telegram.messages;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.models.Token;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class PasswordResetMessage implements TelegramMessage {
    private static final String TAG = "[" + PasswordResetMessage.class.getSimpleName() + "]";
    private final Token token;
    private final String telegramAccount;

    @Override
    public String getTemplate() {
        return "passwordReset.ftl";
    }

    @Override
    public Map<String, Object> getTemplateVars() {
        String link = getLink();
        Map<String, Object> vars = new HashMap<>(1);
        vars.put("link", link);
        vars.put("username", telegramAccount);
        return vars;
    }

    public String getLink() {
        return MessageFormat.format("{0}/{1}?token={2}",
                AppUtils.getShortUrlFromStaticContext(), Endpoint.UI.PASSWORD_RESET_PAGE, token);
    }

}

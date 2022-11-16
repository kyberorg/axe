package io.kyberorg.yalsee.telegram.messages;

import java.util.Map;

/**
 * Base for all messages, send via Telegram.
 */
public interface TelegramMessage {
    /**
     * Get linked template filename. Templates are located at resources/templates/telegram
     *
     * @return string with template filename with extension.
     */
    String getTemplate();

    /**
     * Provides Template Variables.
     *
     * @return {@link Map} with template variables.
     */
    Map<String, Object> getTemplateVars();

}

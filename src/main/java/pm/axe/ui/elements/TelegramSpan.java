package pm.axe.ui.elements;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import pm.axe.Axe;
import pm.axe.db.models.Token;
import pm.axe.telegram.TelegramCommand;
import pm.axe.users.TokenType;
import pm.axe.utils.AppUtils;
import pm.axe.utils.ClipboardUtils;
import pm.axe.utils.DeviceUtils;

/**
 * {@link Span} with message "Send code to link your account with Telegram".
 */
public final class TelegramSpan extends Composite<Span> implements HasStyle {

    /**
     * Creates new {@link TelegramSpan}.
     *
     * @param token {@link TokenType#TELEGRAM_CONFIRMATION_TOKEN} {@link Token}.
     * @return new {@link TelegramSpan} with prefilled fields.
     */
    public static TelegramSpan create(final Token token) {
        return new TelegramSpan(token);
    }

    private TelegramSpan(final Token tgToken) {
        String tgCommand = TelegramCommand.HELLO.getCommandText();

        Span startSpan = new Span("Send ");
        Code tgString = new Code(String.format("%s %s", tgCommand, tgToken.getToken()));
        Icon copyCommandIcon = VaadinIcon.COPY.create();
        Span toSpan = new Span(" to ");

        String botName = AppUtils.getTelegramBotName();
        String telegramLink = String.format("%s%s", Axe.Telegram.TELEGRAM_URL, botName);
        Anchor botLink = new Anchor(telegramLink, "@" + botName);

        Span endSpan = new Span(" to link your account with Telegram.");

        getContent().add(startSpan, tgString, copyCommandIcon, toSpan, botLink, endSpan);

        ClipboardUtils.setCopyToClipboardFunctionFor(copyCommandIcon);
        ClipboardUtils.setTextToCopy(tgString.getText()).forComponent(copyCommandIcon);
        copyCommandIcon.setClassName("copy-command-icon");
        copyCommandIcon.addClickListener(e ->  {
            Notification.Position position = DeviceUtils.isMobileDevice()
                    ? Notification.Position.BOTTOM_CENTER : Notification.Position.MIDDLE;
            ClipboardUtils.showLinkCopiedNotification("Copied!", position);
        });
    }
}

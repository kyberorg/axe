package pm.axe.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;

/**
 * Utils for operating with Clipboard.
 * <p>
 *
 * @see <a href="https://cookbook.vaadin.com/copy-to-clipboard">Vaadin Cookbook</a>
 * @since 3.1.2
 */

@Slf4j
@UIScope
@JsModule("./js/copy-to-clipboard.js")
public final class ClipboardUtils {
    private static final String ON_CLICK = "onclick";
    private static final String ATTRIBUTE_NAME = "text";

    private ClipboardUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Copies text and shows Notification with given text and {@link Notification.Position}.
     *
     * @param textToCopy           string with text to copy to clipboard
     * @param notificationText     string with notification text
     * @param notificationPosition position of notification
     */
    public static void copyToClipboardAndNotify(final String textToCopy, final String notificationText,
                                                final Notification.Position notificationPosition) {
        copyToClipboard(textToCopy);
        getLinkCopiedNotification(notificationText, notificationPosition).open();
    }

    public static void setCopyToClipboardFunction(final Component component) {
        component.getElement().removeAttribute(ON_CLICK);
        component.getElement().setAttribute(ON_CLICK, "copyTextToClipboard(this)");
    }

    public static void setTextToCopy(final String textToCopy, final Component component) {
        component.getElement().removeAttribute(ATTRIBUTE_NAME);
        component.getElement().setAttribute(ATTRIBUTE_NAME, textToCopy);
    }

    /**
     * Creates {@link Notification} that reports, that link is copied.
     *
     * @param notificationText text of {@link Notification}
     * @param notificationPosition {@link Notification.Position} at screen.
     * @return ready to use {@link Notification}, which should be {@link Notification#open()}ed.
     */
    public static Notification getLinkCopiedNotification(final String notificationText,
                                                          final Notification.Position notificationPosition) {
        final int notificationDuration = 3000;
        Notification notification =
                new Notification(notificationText, notificationDuration, notificationPosition);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        return notification;
    }

    private static void copyToClipboard(final String textToCopy) {
        UI.getCurrent().getPage().executeJs("window.copyToClipboard($0)", textToCopy);
    }
}

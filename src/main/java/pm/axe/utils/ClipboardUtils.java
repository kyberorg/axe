package pm.axe.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Utils for operating with Clipboard.
 * <p>
 *
 * @see <a href="https://cookbook.vaadin.com/copy-to-clipboard">Vaadin Cookbook</a>
 * @since 3.1.2
 */

@Slf4j
@JsModule("./js/copy-to-clipboard.js")
public final class ClipboardUtils {
    private static final String ON_CLICK = "onclick";
    private static final String ATTRIBUTE_NAME = "text";

    private ClipboardUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * This method sets onClick function to given {@link Component}.
     * Any existing {@link #ON_CLICK} handler will be removed to avoid conflicts.
     *
     * @param component clickable {@link Button} or {@link Icon} after clicking it, text should be copied to clipboard.
     * @throws IllegalArgumentException when {@link Component} is {@code null}.
     */
    public static void setCopyToClipboardFunctionFor(final Component component) {
        if (Objects.isNull(component)) throw new IllegalArgumentException("component cannot be null");
        component.getElement().removeAttribute(ON_CLICK);
        //this should match function in JsModule
        component.getElement().setAttribute(ON_CLICK, "window.copyTextToClipboard(this)");
    }

    /**
     * Sets text, which should be copied to clipboard, after user clicks on given {@link Component}.
     *
     * @param textToCopy string with text, which be copied to clipboard. Empty string will clear clipboard.
     * @return temporary {@link Component}, which allows to set linked {@link Component} aka copy trigger.
     * See {@link ClipboardComponent#forComponent(Component)}
     */
    public static ClipboardComponent setTextToCopy(final String textToCopy) {
        return new ClipboardComponent(textToCopy);
    }

    /**
     * Creates and shows {@link Notification} that reports, that link is copied.
     *
     * @param notificationText text of {@link Notification}
     * @param notificationPosition {@link Notification.Position} at screen.
     */
    public static void showLinkCopiedNotification(final String notificationText,
                                                          final Notification.Position notificationPosition) {
        final int notificationDuration = 3000;
        Notification notification =
                new Notification(notificationText, notificationDuration, notificationPosition);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    /**
     * Temporary {@link Component} for linking with click trigger {@link Component}.
     */
    public static class ClipboardComponent {
        private final String text;
        private ClipboardComponent(final String textToCopy) {
            this.text = textToCopy;
        }

        /**
         * Set clickable {@link Component} ({@link Button} or {@link Icon}),
         * which will act as copy to clipboard trigger. Clicking on this will run function, which
         * copies text to clipboard.
         *
         * @param component Clickable {@link Component} ({@link Button} or {@link Icon})
         * @throws IllegalArgumentException when {@link Component} is {@code null}.
         */
        public void forComponent(final Component component) {
            if (Objects.isNull(component)) throw new IllegalArgumentException("component cannot be null");
            component.getElement().removeAttribute(ATTRIBUTE_NAME);
            component.getElement().setAttribute(ATTRIBUTE_NAME, this.text);
        }
    }
}

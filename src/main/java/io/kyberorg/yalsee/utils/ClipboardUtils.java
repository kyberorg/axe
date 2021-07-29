package io.kyberorg.yalsee.utils;

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
 * @see https://cookbook.vaadin.com/copy-to-clipboard
 * @since 3.2
 */
@Slf4j
@UIScope
@JsModule("./js/copytoclipboard.js")
public final class ClipboardUtils {

    private ClipboardUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Copies link to Clipboard and shows notification.
     *
     * @param link string with link to copy
     */
    public static void copyLinkToClipboard(final String textToCopy) {
        getLinkCopiedNotification().open();
    }

    private static Notification getLinkCopiedNotification() {
        final int notificationDuration = 3000;
        Notification notification =
                new Notification("Short link copied", notificationDuration, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        return notification;
    }

    /**
     * Copies given text to Clipboard.
     *
     * @param textToCopy string to copy
     */
    private static void copyToClipboard(final String textToCopy) {
        UI.getCurrent().getPage().executeJs("window.copyToClipboard($0)", textToCopy);
    }
}

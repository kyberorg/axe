package io.kyberorg.yalsee.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.concurrent.atomic.AtomicBoolean;

@UIScope
public final class DeviceUtils {
    private static final int EXTRA_SMALL_WIDTH_BREAKPOINT_PIXELS = 576;

    private DeviceUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Detects if client runs on extra small devices (portrait phones, less than 576px).
     *
     * @return true - if client device is extra small, false - if not.
     */
    public static boolean isExtraSmallDevice() {
        AtomicBoolean isExtraSmallDevice = new AtomicBoolean(false);
        if (UI.getCurrent() == null || UI.getCurrent().getPage() == null) {
            return isExtraSmallDevice.get();
        }
        UI.getCurrent().getPage()
                .retrieveExtendedClientDetails(e -> isExtraSmallDevice.set(e.getScreenWidth() <= EXTRA_SMALL_WIDTH_BREAKPOINT_PIXELS));
        return isExtraSmallDevice.get();
    }
}

package io.kyberorg.yalsee.utils;

import com.helger.commons.exception.InitializationException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.UIScope;

@UIScope
public final class DeviceUtils {
    private static final int EXTRA_SMALL_WIDTH_BREAKPOINT_PIXELS = 576;

    private int screenWidth;

    public static DeviceUtils createWithUI(final UI ui) {
        try {
            return new DeviceUtils(ui);
        } catch (InitializationException e) {
            return null;
        }
    }

    private DeviceUtils(UI ui) throws InitializationException {
        if (ui == null || ui.getPage() == null) {
            throw new InitializationException("Provided UI or its Page is empty");
        }
        ui.getPage().retrieveExtendedClientDetails(e -> this.screenWidth = e.getScreenWidth());
    }

    /**
     * Detects if client runs on extra small devices (portrait phones, less than 576px).
     *
     * @return true - if client device is extra small, false - if not.
     */
    public boolean isExtraSmallDevice() {
        return screenWidth > 0 && screenWidth <= EXTRA_SMALL_WIDTH_BREAKPOINT_PIXELS;
    }
}

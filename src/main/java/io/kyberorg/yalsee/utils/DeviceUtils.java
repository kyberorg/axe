package io.kyberorg.yalsee.utils;

import com.helger.commons.exception.InitializationException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.ExtendedClientDetails;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.shared.BrowserDetails;
import com.vaadin.flow.spring.annotation.UIScope;

/**
 * Client Device-related stuff.
 *
 * @since 3.2
 */
@UIScope
public final class DeviceUtils {
    private static final int EXTRA_SMALL_WIDTH_BREAKPOINT_PIXELS = 576;

    private int screenWidth;
    private boolean areDetailsSet;

    /**
     * Creates {@link DeviceUtils} object with given {@link UI} object.
     * {@link UI} object need to retrieve information from.
     *
     * @param ui valid {@link UI} object need to retrieve information from.
     * @return {@link DeviceUtils} object with information from {@link UI}
     * or {@code null}, if provided {@link UI} object is not valid or doesn't have {@link Page}
     */
    public static DeviceUtils createWithUI(final UI ui) {
        try {
            return new DeviceUtils(ui);
        } catch (InitializationException e) {
            return null;
        }
    }

    /**
     * Defines if client device is mobile device. User-Agent header is used to define device.
     *
     * @param userAgent string with UserAgent aka client's browser. Can be retrieved from current session.
     * @return true if client device is Android, iPhone or Windows Phone and false is not.
     */
    public static boolean isMobileDevice(final String userAgent) {
        BrowserDetails browserDetails = new BrowserDetails(userAgent);
        return browserDetails.isAndroid() || browserDetails.isIPhone() || browserDetails.isWindowsPhone();
    }

    private DeviceUtils(final UI ui) throws InitializationException {
        if (ui == null || ui.getPage() == null) {
            throw new InitializationException("Provided UI or its Page is empty");
        }
        ui.getPage().retrieveExtendedClientDetails(this::setDetails);
    }

    /**
     * Determines is client details were set or not.
     *
     * @return true - if details were set, else false.
     */
    public boolean areDetailsSet() {
        return areDetailsSet;
    }

    /**
     * Detects if client runs on extra small devices (portrait phones, less than 576px).
     *
     * @return true - if client device is extra small, false - if not.
     */
    public boolean isExtraSmallDevice() {
        return screenWidth > 0 && screenWidth <= EXTRA_SMALL_WIDTH_BREAKPOINT_PIXELS;
    }

    private void setDetails(final ExtendedClientDetails clientDetails) {
        this.screenWidth = clientDetails.getScreenWidth();
        this.areDetailsSet = true;
    }
}

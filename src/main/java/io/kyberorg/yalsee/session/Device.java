package io.kyberorg.yalsee.session;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.WebBrowser;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.services.RobotsService;
import io.kyberorg.yalsee.utils.DeviceUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * Information about Device (most often Browser).
 *
 * @since 3.8
 */
@Data
public class Device implements Serializable {
    /**
     * Placeholder for UserAgent. Used when UA is unknown or undefined.
     */
    public static final String DEFAULT_USER_AGENT = "Unknown Browser";

    /**
     * Placeholder for IP address. Used when IP is unknown or undefined.
     */
    public static final String DEFAULT_IP = "0.0.0.0";

    /**
     * IP address for localhost aka internal connection.
     */
    private static final String LOCALHOST_IP = "127.0.0.1";

    private String userAgent = DEFAULT_USER_AGENT;
    private String ip = DEFAULT_IP;
    private boolean secureConnection = false;
    private boolean mobile = false;
    private transient boolean robot = false;

    /**
     * Creates Device based information from {@link VaadinRequest} and {@link WebBrowser}.
     *
     * @param request non-empty current {@link VaadinRequest}.
     * @param browser non-empty {@link WebBrowser} object.
     * @return created Device object.
     */
    public static Device from(final VaadinRequest request, final WebBrowser browser) {
        if (browser == null) {
            return Device.withDefaults();
        } else {
            String userAgent = browser.getBrowserApplication();
            String ip = null;
            String forwardedProtoHeader = null;
            if (request != null) {
                ip = request.getHeader(Header.X_REAL_IP);
                forwardedProtoHeader = request.getHeader(Header.X_FORWARDED_PROTO);
            }

            if (StringUtils.isBlank(ip)) {
                ip = browser.getAddress();
            }

            Device device = new Device();
            boolean hasForwardedProtoHeader = StringUtils.isNotBlank(forwardedProtoHeader);
            if (hasForwardedProtoHeader) {
                device.setSecureConnection(forwardedProtoHeader.equalsIgnoreCase("https"));
            } else {
                device.setSecureConnection(browser.isSecureConnection());
            }

            if (StringUtils.isNotBlank(userAgent)) {
                device.setUserAgent(userAgent);
            }
            if (StringUtils.isNotBlank(ip)) {
                device.setIp(ip);
            }

            if (RobotsService.getInstance() != null) {
                device.setRobot(RobotsService.getInstance().isRobot(device.getUserAgent()));
            } else {
                device.setRobot(false);
            }

            device.setMobile(DeviceUtils.isMobileDevice(userAgent));

            return device;
        }
    }

    /**
     * Compares Devices.
     *
     * @param another another Device to compare with.
     * @return true - if {@link #userAgent} equals another, false if not.
     */
    public boolean isSameDevice(final Device another) {
        return userAgent.equals(another.getUserAgent());
    }

    /**
     * Defines if connection is from the same server aka health-checks etc.
     *
     * @return true - if connection has local IP address, false is not.
     */
    public boolean isInternal() {
        if (StringUtils.isBlank(this.getIp())) return false;
        return this.getIp().equals(LOCALHOST_IP);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return isSameDevice(device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userAgent, ip, secureConnection, robot);
    }

    private static Device withDefaults() {
        return new Device();
    }
}

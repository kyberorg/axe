package io.kyberorg.yalsee.session;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.WebBrowser;
import io.kyberorg.yalsee.constants.Header;
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

    private String userAgent = DEFAULT_USER_AGENT;
    private String ip = DEFAULT_IP;
    private boolean secureConnection = false;
    private boolean robot = false;
    private WebBrowser browser;

    /**
     * Creates {@link Device} based information from {@link VaadinRequest} and {@link WebBrowser}.
     *
     * @param request non-empty current {@link VaadinRequest}.
     * @param browser non-empty {@link WebBrowser} object.
     * @return created {@link Device} object.
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

            device.browser = browser;
            device.setRobot(RobotsList.isRobot(device.getUserAgent()));

            return device;
        }
    }

    /**
     * Compares Devices.
     *
     * @param other other {@link Device}.
     * @return true - if both {@link #userAgent} and {@link #ip} are equal, false if not.
     */
    public boolean isSameDevice(final Device other) {
        return userAgent.equals(other.getUserAgent()) && ip.equals(other.ip);
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

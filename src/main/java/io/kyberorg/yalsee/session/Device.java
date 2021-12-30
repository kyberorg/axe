package io.kyberorg.yalsee.session;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.WebBrowser;
import io.kyberorg.yalsee.constants.Header;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
public class Device implements Serializable {
    public static final String DEFAULT_USER_AGENT = "Unknown Browser";
    public static final String DEFAULT_IP = "0.0.0.0";

    private String userAgent = DEFAULT_USER_AGENT;
    private String ip = DEFAULT_IP;

    @JsonIgnore
    private WebBrowser webBrowser = null;

    public static Device from(final VaadinRequest request, final WebBrowser browser) {
        if (browser == null) {
            return Device.withDefaults();
        } else {
            String userAgent = browser.getBrowserApplication();
            String ip = null;
            if (request != null) {
                ip = request.getHeader(Header.X_REAL_IP);
            }

            if (StringUtils.isBlank(ip)) {
                ip = browser.getAddress();
            }

            Device device = new Device();
            device.setWebBrowser(browser);

            if (StringUtils.isNotBlank(userAgent)) {
                device.setUserAgent(userAgent);
            }
            if (StringUtils.isNotBlank(ip)) {
                device.setIp(ip);
            }
            return device;
        }
    }

    private static Device withDefaults() {
        return new Device();
    }

    public boolean isSameDevice(Device other) {
        return userAgent.equals(other.getUserAgent()) && ip.equals(other.ip);
    }
}

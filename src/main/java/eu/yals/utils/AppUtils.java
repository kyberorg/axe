package eu.yals.utils;

import com.google.common.base.CharMatcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;
import eu.yals.constants.App;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * App-wide tools
 *
 * @since 1.0
 */
@Slf4j
@Component
public class AppUtils {

    private final Environment env;

    public static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static final String DUMMY_HOST = "DummyHost";
    private static final String DUMMY_TOKEN = "dummyToken";

    public static boolean clientWantsJson(VaadinRequest vaadinRequest) {
        String acceptHeader = vaadinRequest.getHeader(Header.ACCEPT);
        return clientWantsJson(acceptHeader);
    }

    public static boolean clientWantsJson(HttpServletRequest request) {
        String acceptHeader = request.getHeader(Header.ACCEPT);
        return clientWantsJson(acceptHeader);
    }

    public static boolean isApiRequest(HttpServletRequest request) {
        String requestUrl;
        try {
            requestUrl = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        } catch (Exception e) {
            log.error("Failed to determine request URL which caused an error", e);
            return false;
        }
        return requestUrl.startsWith("/api");
    }

    public static boolean hasAcceptHeader(HttpServletRequest req) {
        boolean acceptHeaderPresent = StringUtils.isNotBlank(req.getHeader(Header.ACCEPT));
        if (acceptHeaderPresent) {
            boolean hasExactMimeType = !req.getHeader(Header.ACCEPT).equals(MimeType.ALL);
            return hasExactMimeType;
        } else {
            return false;
        }
    }

    /**
     * This is needed to handle shit like {@literal text/html,application/xhtml+xml,application/xml;q=0.9,**;q=0.8}
     *
     * @param req request
     * @return true - any part Accept Header contains {@link MimeType#TEXT_HTML}, false elsewhere
     */
    public static boolean clientWantsHtml(HttpServletRequest req) {
        boolean acceptHeaderPresent = StringUtils.isNotBlank(req.getHeader(Header.ACCEPT));
        if (acceptHeaderPresent) {
            String rawAcceptHeader = req.getHeader(Header.ACCEPT);
            String[] parts = rawAcceptHeader.split(",");
            if (parts.length < 1) {
                return false;
            }
            for (String mimeType : parts) {
                if (mimeType.equals(MimeType.TEXT_HTML)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public AppUtils(Environment env) {
        this.env = env;
    }

    public String getAPIHostPort() {
        return "localhost" + ":" + env.getProperty(App.Properties.SERVER_PORT, "8080");
    }

    public String getServerUrl() {
        String serverUrl = env.getProperty(App.Properties.SERVER_URL);
        return StringUtils.isNotBlank(serverUrl) ? serverUrl : DUMMY_HOST;
    }

    public String getTelegramToken() {
        String token = env.getProperty(App.Properties.TELEGRAM_TOKEN);
        return StringUtils.isNotBlank(token) ? token : DUMMY_TOKEN;
    }

    /**
     * Checks string if it has ASCII symbols or not
     *
     * @param str any string
     * @return true if contains only ASCII (std latin) symbols, false elsewhere
     */
    public static boolean isAscii(String str) {
        return CharMatcher.ascii().matchesAllOf(str);
    }

    /**
     * Makes fully qualified URI resource from string with url.
     *
     * @param url string with valid URL
     * @return URI from same URL if URL already has schema or URI from default http schema and requested URL
     * @throws RuntimeException if string has not valid URL or not URL
     */
    public static URI makeFullUri(String url) {
        try {
            URI uri = new URI(url);

            if (uri.getScheme() == null) {
                uri = new URI("http://" + url);
            }
            return uri;
        } catch (URISyntaxException e) {
            String message = String.format("String '%s': malformed URL or not URL at all", url);
            log.warn(message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Code taken from {@link https://nealvs.wordpress.com/2016/01/18/how-to-convert-unicode-url-to-ascii-in-java/}
     *
     * @param url string with valid URL to convert
     * @return is URL contains only ASCII chars - same URL, otherwise punycoded URL,
     * @throws RuntimeException if URL malformed or not URL
     */
    public static String covertUnicodeToAscii(String url) {
        if (url != null) {
            url = url.trim();

            // Handle international domains by detecting non-ascii and converting them to punycode
            if (isAscii(url)) return url;

            URI uri;
            try {
                uri = makeFullUri(url);

                String scheme = uri.getScheme() != null ? uri.getScheme() + "://" : null;
                String authority = uri.getRawAuthority() != null ? uri.getRawAuthority() : ""; // includes domain and port
                String path = uri.getRawPath() != null ? uri.getRawPath() : "";
                String queryString = uri.getRawQuery() != null ? "?" + uri.getRawQuery() : "";

                // Must convert domain to punycode separately from the path
                url = scheme + IDN.toASCII(authority) + path + queryString;
                // Convert path from unicode to ascii encoding
                url = new URI(url).toASCIIString();
            } catch (URISyntaxException e) {
                String message = String.format("String '%s': malformed URL or not URL at all", url);
                log.warn(message);
                throw new RuntimeException(message, e);
            }
        }
        return url;
    }

    /**
     * Decodes URL from wiki/%D0%9E%D1%80%D0%B5%D1%81%D1%82 to wiki/Орест
     *
     * @param encodedUrl string with URL where encoded chars are present or not
     * @return string with decoded URL or same string if URL has no chars to encode
     */
    public static String decodeUrl(String encodedUrl) {
        try {
            return URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("Failed to decode URL", e);
            throw new RuntimeException(e.getCause());
        }
    }

    public boolean isTelegramDisabled() {
        return !Boolean.parseBoolean(env.getProperty(App.Properties.TELEGRAM_ENABLED, "false"));
    }

    public boolean isMobile(VaadinSession vaadinSession) {
        WebBrowser browser = vaadinSession.getBrowser();
        assert browser != null;
        return browser.isIOS() || browser.isAndroid();
    }

    public boolean isNotMobile(VaadinSession vaadinSession) {
        return !isMobile(vaadinSession);
    }

    private static boolean clientWantsJson(String acceptHeader) {
        if (acceptHeader == null) {
            return false;
        } else {
            return acceptHeader.equals(MimeType.APPLICATION_JSON);
        }

    }
}

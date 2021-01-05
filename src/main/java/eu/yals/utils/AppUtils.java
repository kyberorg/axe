package eu.yals.utils;

import com.google.common.base.CharMatcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinRequest;
import eu.yals.constants.App;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import lombok.Getter;
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
 * App-wide tools.
 *
 * @since 1.0
 */
@Slf4j
@Component
public class AppUtils {
    private static final String TAG = "[" + AppUtils.class.getSimpleName() + "]";
    @Getter
    private final Environment env;

    public static final Gson GSON = new GsonBuilder().serializeNulls().create();
    public static final String HTML_MODE = "innerHTML";
    private static final String DUMMY_HOST = "DummyHost";
    private static final String DUMMY_TOKEN = "dummyToken";

    /**
     * Creates {@link AppUtils}.
     *
     * @param env environment variables
     */
    public AppUtils(final Environment env) {
        this.env = env;
    }

    /**
     * Determine if client wants to receive JSON from us.
     *
     * @param request valid {@link HttpServletRequest} request
     * @return true, if clients {@link Header#ACCEPT} contains {@link MimeType#APPLICATION_JSON} mime type,
     * false - elsewhere
     */
    public static boolean clientWantsJson(final HttpServletRequest request) {
        String acceptHeader = request.getHeader(Header.ACCEPT);
        return clientWantsJson(acceptHeader);
    }

    /**
     * Determines if client wants to receive JSON from us. Vaadin implementation.
     *
     * @param vaadinRequest valid {@link VaadinRequest} from VaadinServlet
     * @return true, if clients {@link Header#ACCEPT} contains {@link MimeType#APPLICATION_JSON} mime type,
     * false - elsewhere
     */
    public static boolean clientWantsJson(final VaadinRequest vaadinRequest) {
        String acceptHeader = vaadinRequest.getHeader(Header.ACCEPT);
        return clientWantsJson(acceptHeader);
    }

    /**
     * Determines if request came from API or not.
     *
     * @param request valid {@link HttpServletRequest} request
     * @return true, if request contains api part in URL, false - elsewhere
     */
    public static boolean isApiRequest(final HttpServletRequest request) {
        String requestUrl;
        try {
            requestUrl = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        } catch (Exception e) {
            log.error("{} Failed to determine request URL which caused an error", TAG);
            log.debug("", e);
            return false;
        }
        return requestUrl.startsWith("/api");
    }

    /**
     * Determines if client's request has {@link Header#ACCEPT} header with exact {@link MimeType}.
     *
     * @param req valid {@link HttpServletRequest} request
     * @return true, if request has {@link Header#ACCEPT} header and it value is not wildcard
     * (aka accept all) {@link MimeType#ALL}, false - elsewhere
     */
    public static boolean hasAcceptHeader(final HttpServletRequest req) {
        boolean acceptHeaderPresent = StringUtils.isNotBlank(req.getHeader(Header.ACCEPT));
        if (acceptHeaderPresent) {
            @SuppressWarnings("UnnecessaryLocalVariable") //increase readability
                    boolean hasExactMimeType = !req.getHeader(Header.ACCEPT).equals(MimeType.ALL);
            return hasExactMimeType;
        } else {
            return false;
        }
    }

    /**
     * This is needed to handle shit like {@literal text/html,application/xhtml+xml,application/xml;q=0.9,**;q=0.8}.
     *
     * @param req request
     * @return true - any part Accept Header contains {@link MimeType#TEXT_HTML}, false elsewhere
     */
    public static boolean clientWantsHtml(final HttpServletRequest req) {
        boolean acceptHeaderPresent = StringUtils.isNotBlank(req.getHeader(Header.ACCEPT));
        if (!acceptHeaderPresent) {
            return false;
        }
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
    }

    /**
     * Provides URL base for calling API from application internally.
     *
     * @return string (https://localhost:<port>)
     */
    public String getAPIHostPort() {
        return "http://localhost" + ":" + env.getProperty(App.Properties.SERVER_PORT, "8080");
    }


    /**
     * Checks string if it has ASCII symbols or not.
     *
     * @param str any string
     * @return true if contains only ASCII (std latin) symbols, false elsewhere
     */
    public static boolean isAscii(final String str) {
        return CharMatcher.ascii().matchesAllOf(str);
    }

    /**
     * Makes fully qualified URI resource from string with url.
     *
     * @param url string with valid URL
     * @return URI from same URL if URL already has schema or URI from default http schema and requested URL
     * @throws RuntimeException if string has not valid URL or not URL
     */
    public static URI makeFullUri(final String url) {
        try {
            URI uri = new URI(replaceSpacesInUrl(url));

            if (uri.getScheme() == null) {
                uri = new URI("http://" + url);
            }
            return uri;
        } catch (URISyntaxException e) {
            String message = String.format("String '%s': malformed URL or not URL at all", url);
            log.warn("{} {}", TAG, message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Code taken from {@link https://nealvs.wordpress.com/2016/01/18/how-to-convert-unicode-url-to-ascii-in-java/}.
     *
     * @param url string with valid URL to convert
     * @return is URL contains only ASCII chars - same URL, otherwise punycoded URL,
     * @throws RuntimeException if URL malformed or not URL
     */
    public static String covertUnicodeToAscii(final String url) {
        if (url == null) return null;

        String trimUrl = url.trim();

        // Handle international domains by detecting non-ascii and converting them to punycode
        if (isAscii(trimUrl)) return trimUrl;

        URI uri;
        try {
            uri = makeFullUri(trimUrl);

            String scheme = uri.getScheme() != null ? uri.getScheme() + "://" : null;
            // includes domain and port
            String authority = uri.getRawAuthority() != null ? uri.getRawAuthority() : "";
            String path = uri.getRawPath() != null ? uri.getRawPath() : "";
            String queryString = uri.getRawQuery() != null ? "?" + uri.getRawQuery() : "";

            // Must convert domain to punycode separately from the path
            trimUrl = scheme + IDN.toASCII(authority) + path + queryString;
            // Convert path from unicode to ascii encoding
            trimUrl = new URI(trimUrl).toASCIIString();
        } catch (URISyntaxException e) {
            String message = String.format("String '%s': malformed URL or not URL at all", url);
            log.warn("{} {}", TAG, message);
            throw new RuntimeException(message, e);
        }
        return trimUrl;
    }

    /**
     * Decodes URL from wiki/%D0%9E%D1%80%D0%B5%D1%81%D1%82 to wiki/Орест.
     *
     * @param encodedUrl string with URL where encoded chars are present or not
     * @return string with decoded URL or same string if URL has no chars to encode
     */
    public static String decodeUrl(final String encodedUrl) {
        try {
            return URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("{} Failed to decode URL", TAG);
            log.debug("", e);
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * Creates Modal in the middle of page.
     *
     * @param text string with error
     * @param variant valid {@link NotificationVariant}
     * @return created {@link Notification}
     */
    public static Notification getNotification(final String text, final NotificationVariant variant) {
        Notification notification = new Notification();
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.MIDDLE);

        HorizontalLayout layout = new HorizontalLayout();
        Label label = new Label(text);
        Button closeButton = new Button("OK", event -> notification.close());

        label.getStyle().set("margin-right", "0.5rem");
        closeButton.getStyle().set("margin-right", "0.5rem");

        layout.add(label, closeButton);
        notification.add(layout);
        return notification;
    }

    /**
     * Provides url of server. Runtime value.
     *
     * @return server url from env if found or {@link #DUMMY_HOST}
     */
    public String getServerUrl() {
        String serverUrl = env.getProperty(App.Properties.SERVER_URL);
        return StringUtils.isNotBlank(serverUrl) ? serverUrl : DUMMY_HOST;
    }

    /**
     * Provides token for telegram.
     *
     * @return token from env if found or {@link #DUMMY_TOKEN}.
     */
    public String getTelegramToken() {
        String token = env.getProperty(App.Properties.TELEGRAM_TOKEN);
        return StringUtils.isNotBlank(token) ? token : DUMMY_TOKEN;
    }

    /**
     * Defines is telegram integration is enabled or not.
     *
     * @return {@link App.Properties#TELEGRAM_ENABLED} property value or false
     */
    public boolean isTelegramDisabled() {
        return !Boolean.parseBoolean(env.getProperty(App.Properties.TELEGRAM_ENABLED, "false"));
    }

    /**
     * Defines if application works in Development Mode. Dev Mode enables developer features (i.e. Debug Page)
     *
     * @return true if Dev Mode is activated, else false
     */
    public boolean isDevelopmentModeActivated() {
        return Boolean.parseBoolean(env.getProperty(App.Properties.DEV_MODE, "false"));
    }

    /**
     * Checks if {@link Header#X_DEVELOPER} header is present.
     *
     * @return true if header is present, false if not.
     */
    public boolean hasDevHeader() {
        String xDeveloper = VaadinRequest.getCurrent().getHeader(Header.X_DEVELOPER);
        if (StringUtils.isBlank(xDeveloper)) {
            return false;
        } else {
            return Boolean.parseBoolean(xDeveloper);
        }
    }

    /**
     * Pastes HTML to Vaadin Component.
     *
     * @param stringWithHtml string with HTML code.
     * @param component      vaadin component
     */
    public void pasteHtmlToComponent(final String stringWithHtml,
                                     final com.vaadin.flow.component.Component component) {
        component.getElement().setProperty(HTML_MODE, stringWithHtml);
    }

    private static boolean clientWantsJson(final String acceptHeader) {
        if (acceptHeader == null) {
            return false;
        } else {
            return acceptHeader.equals(MimeType.APPLICATION_JSON);
        }
    }

    private static String replaceSpacesInUrl(final String originUrl) {
        return originUrl.replaceAll(" ", "+");
    }

}

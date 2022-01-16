package io.kyberorg.yalsee.utils;

import com.google.common.base.CharMatcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.session.YalseeSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * App-wide tools.
 *
 * @since 1.0
 */
@Slf4j
@Component
public class AppUtils implements Serializable {
    private static final String TAG = "[" + AppUtils.class.getSimpleName() + "]";
    @Getter
    private final Environment env;

    /**
     * This field is dirty hack to access Short URL from static context.
     * To be populated with {@link #populateStaticFields()}
     */
    private static String shortUrl;

    /**
     * This field is dirty hack to access Session Timeout from static context.
     * To be populated with {@link #populateStaticFields()}
     */
    private static int sessionTimeout;

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
        populateStaticFields();
    }

    /**
     * Figures out hostname of host/container where application runs.
     *
     * @return string with hostname or {@code Unknown}.
     */
    public static String getHostname() {
        // Ideally, we'd use InetAddress.getLocalHost, but this does a reverse DNS lookup. On Windows
        // and Linux this is apparently pretty fast, so we don't get random hangs. On OS X it's
        // amazingly slow. That's less than ideal. Figure things out and cache.
        String host = System.getenv("HOSTNAME");  // Most OSs
        if (host == null) {
            host = System.getenv("COMPUTERNAME");  // Windows
        }
        if (host == null && SystemUtils.IS_OS_MAC) {
            try {
                Process process = Runtime.getRuntime().exec("hostname");

                if (!process.waitFor(2, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                    // According to the docs for `destroyForcibly` this is a good idea.
                    process.waitFor(2, TimeUnit.SECONDS);
                }
                if (process.exitValue() == 0) {
                    try (InputStreamReader isr =
                                 new InputStreamReader(process.getInputStream(), Charset.defaultCharset());
                         BufferedReader reader = new BufferedReader(isr)) {
                        host = reader.readLine();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (Exception e) {
                // fall through
            }
        }
        if (host == null) {
            // Give up.
            try {
                host = InetAddress.getLocalHost().getHostName();
            } catch (Exception e) {
                host = "Unknown";  // At least we tried.
            }
        }
        return host;
    }

    private void populateStaticFields() {
        shortUrl = getShortUrl();
        sessionTimeout = getSessionTimeout();
    }

    /**
     * Dirty hack to obtain Server's Short URL from non-Spring objects.
     *
     * @return string with short URL.
     */
    public static String getShortUrlFromStaticContext() {
        return shortUrl;
    }

    /**
     * Dirty hack to obtain Session Timeout from non-Spring objects.
     *
     * @return int with session timeout.
     */
    public static int getSessionTimeoutFromStaticContext() {
        return sessionTimeout;
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
     * Determines if client wants to receive JSON from us. Vaadin's implementation.
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
     * @return true, if request has {@link Header#ACCEPT} header and its value is not wildcard
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
     * Checks string if it has ASCII symbols or not.
     *
     * @param str any string
     * @return true if contains only ASCII (std latin) symbols, false elsewhere
     */
    public static boolean isAscii(final String str) {
        return CharMatcher.ascii().matchesAllOf(str);
    }


    /**
     * Creates Modal in the middle of page.
     *
     * @param text    string with error
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
     * Creates Session Expired Notification.
     *
     * @param ui non-empty {@link UI} to refresh page.
     * @return created {@link Notification}.
     */
    public static Notification getSessionExpiredNotification(final UI ui, final YalseeSession session) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        notification.setPosition(Notification.Position.TOP_STRETCH);

        boolean isMobileDevice = false;
        if (session != null && session.getDevice() != null
                && StringUtils.isNotBlank(session.getDevice().getUserAgent())) {
            String ua = session.getDevice().getUserAgent();
            isMobileDevice = ua.contains("iPhone") || ua.contains("Android") || ua.contains("WindowsMobile");
        }
        //TODO check UA, when UA parser is ready

        String message;
        if (isMobileDevice) {
            message = "Session expires soon. Any unsaved data will be lost.";
        } else {
            message = String.format("Your session expires in %d minutes. Take note of any unsaved data.",
                    YalseeSession.TIMEOUT_FOR_WARNING_MINUTES);
            Shortcuts.addShortcutListener(notification, notification::close, Key.ESCAPE);
        }

        Div text = new Div(new Text(message));

        Button pageRefreshButton = new Button("Refresh Page", event -> ui.getPage().reload());
        pageRefreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        Button closeButton = new Button("Dismiss", event -> notification.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        HorizontalLayout layout;
        if (isMobileDevice) {
            layout = new HorizontalLayout(text, closeButton);
            layout.setAlignItems(FlexComponent.Alignment.AUTO);
            closeButton.setText("OK");

        } else {
            layout = new HorizontalLayout(text, pageRefreshButton, closeButton);
            layout.setAlignItems(FlexComponent.Alignment.AUTO);
            layout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        }
        notification.add(layout);
        return notification;
    }

    /**
     * Current {@link Date}.
     *
     * @return {@link Date} object of now.
     */
    public static Date now() {
        return Date.from(Instant.now());
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
     * Provides short url used for links. Runtime value.
     *
     * @return string with short url (scheme + short domain) if found or Server Url from {@link #getServerUrl()}
     */
    public String getShortUrl() {
        String shortDomain = env.getProperty(App.Properties.SHORT_DOMAIN, DUMMY_HOST);
        if (shortDomain.equals(DUMMY_HOST)) {
            //no short URL - use server URL
            log.debug("No Short Domain defined - using Server URL");
            return getServerUrl();
        } else {
            URI serverUri = UrlUtils.makeFullUri(getServerUrl());
            String scheme = serverUri.getScheme() != null ? serverUri.getScheme() + "://" : "http://";
            return scheme + shortDomain;
        }
    }

    /**
     * Provides host of running instance. Runtime value.
     *
     * @return string with full domain
     */
    public String getServerDomain() {
        return UrlUtils.removeProtocol(getServerUrl());
    }

    /**
     * Provides host of short url used for links. Runtime value.
     *
     * @return string with short domain, if found or Server Url from {@link #getServerUrl()}
     */
    public String getShortDomain() {
        String shortDomain = env.getProperty(App.Properties.SHORT_DOMAIN, DUMMY_HOST);
        if (shortDomain.equals(DUMMY_HOST)) {
            //no short URL - use server domain
            log.debug("No Short Domain defined - using Server Domain");
            return getServerDomain();
        } else {
            return shortDomain;
        }
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

    /**
     * Provides Google Analytics configuration for running environment.
     *
     * @return true - if Google Analytics enabled, false if not.
     */
    public boolean isGoogleAnalyticsEnabled() {
        return Boolean.parseBoolean(getEnv().getProperty(App.Properties.GA_ENABLED));
    }

    /**
     * Provides filename of html, where Google Analytics stored.
     *
     * @return string with filename and extension read from env
     */
    public String getGoggleAnalyticsFileName() {
        return getEnv().getProperty(App.Properties.GA_FILE);
    }

    /**
     * Allow/Disallow crawlers (search engine bots), based on profile settings.
     *
     * @return true - if crawlers should be allowed, false - elsewhere
     */
    public boolean areCrawlersAllowed() {
        return Boolean.parseBoolean(getEnv().getProperty(App.Properties.CRAWLERS_ALLOWED));
    }

    /**
     * Provides Delete Token for EnvVar if any.
     *
     * @return string with token, or {@link App#NO_VALUE}.
     */
    public String getDeleteToken() {
        return getEnv().getProperty(App.Env.DELETE_TOKEN, App.NO_VALUE);
    }

    /**
     * Reads redirect page bypass symbol from settings.
     *
     * @return string with skip mark or {@link App#NO_VALUE}
     */
    public String getRedirectPageBypassSymbol() {
        return getEnv().getProperty(App.Properties.REDIRECT_PAGE_BYPASS_SYMBOL, App.NO_VALUE);
    }

    /**
     * Determines if ident has redirect page bypass symbol at the end.
     *
     * @param ident string with ident to check
     * @return true - if ident has bypass symbol, false - if not
     */
    public boolean hasRedirectPageBypassSymbol(final String ident) {
        return ident.endsWith(getRedirectPageBypassSymbol());
    }

    /**
     * Drops redirect page bypass symbol from provided ident string. It also checks if string has this symbol.
     *
     * @param ident string with ident to check on bypass symbol.
     * @return ident string without bypass symbol or same string, if ident hasn't bypass symbol.
     */
    public String dropRedirectPageBypassSymbolFrom(final String ident) {
        if (hasRedirectPageBypassSymbol(ident)) {
            //remove only last char if it is skip mark
            return ident.substring(0, ident.lastIndexOf(getRedirectPageBypassSymbol()));
        } else {
            return ident;
        }
    }

    /**
     * Reads redirect page skip from settings.
     *
     * @return int with timeout from settings or default timeout {@link App.Defaults#REDIRECT_PAGE_TIMEOUT_SECONDS}
     */
    public int getRedirectPageTimeout() {
        String timeoutString = getEnv().getProperty(App.Properties.REDIRECT_PAGE_TIMEOUT, App.NO_VALUE);
        if (timeoutString.equals(App.NO_VALUE)) {
            return App.Defaults.REDIRECT_PAGE_TIMEOUT_SECONDS;
        }
        return Integer.parseInt(timeoutString);
    }

    /**
     * Reads Session Timeout from settings.
     *
     * @return int with  timeout from settings or default timeout {@link App.Defaults#SESSION_TIMEOUT_SECONDS}
     */
    public int getSessionTimeout() {
        String timeoutString = getEnv().getProperty(App.Properties.SESSION_TIMEOUT, App.NO_VALUE);
        if (timeoutString.equals(App.NO_VALUE)) {
            return App.Defaults.SESSION_TIMEOUT_SECONDS;
        }
        return Integer.parseInt(timeoutString);
    }

    /**
     * Ends Vaadin Session. Invalidates bound {@link HttpSession} if any and closes {@link VaadinSession}.
     *
     * @param session {@link VaadinSession} to remove.
     */
    public void endVaadinSession(final VaadinSession session) {
        if (session.getSession() != null) {
            session.getSession().invalidate();
        }
        session.close();
    }

    /**
     * Figures out if analytics cookies are allowed in given session.
     *
     * @param yalseeSession session object to read attribute from.
     * @return true - if analytics cookies are allowed, false - if not.
     */
    public boolean isGoogleAnalyticsAllowed(final Optional<YalseeSession> yalseeSession) {
        return yalseeSession.map(session -> session.getSettings().isAnalyticsCookiesAllowed()).orElse(true);
    }

    /**
     * Defines if url is our internal URL aka same host as server runs at.
     *
     * @param urlString string with valid URL to check
     * @return true - if url is from same host as server runs at, false - if external.
     */
    public boolean isInternalUrl(final String urlString) {
        try {
            final URI uri = new URI(UrlUtils.covertUnicodeUrlToAscii(urlString));
            final String host = uri.getHost();

            boolean matchesServerDomain = host.equals(getServerDomain());
            boolean matchesShortDomain = host.equals(getShortDomain());
            return matchesServerDomain || matchesShortDomain;
        } catch (URISyntaxException e) {
            String message = String.format("String '%s': malformed URL or not URL at all", urlString);
            log.warn("{} {}", TAG, message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Searches Cookie by its name from {@link VaadinRequest}.
     *
     * @param cookieName    non-empty string with cookie name.
     * @param vaadinRequest request to search in.
     * @return found {@link Cookie} object or {@code null}
     */
    public Cookie getCookieByName(final String cookieName, final VaadinRequest vaadinRequest) {
        if (Objects.isNull(cookieName) || Objects.isNull(vaadinRequest)) {
            return null;
        }
        Cookie[] cookies = vaadinRequest.getCookies();
        if (cookies == null || cookies.length == 0) return null;
        for (Cookie cookie : cookies) {
            boolean cookieHasName = (cookie != null && cookie.getName() != null);
            if (cookieHasName && cookie.getName().equals(cookieName)) {
                return cookie;
            }
        }
        return null;
    }

    private static boolean clientWantsJson(final String acceptHeader) {
        if (acceptHeader == null) {
            return false;
        } else {
            return acceptHeader.equals(MimeType.APPLICATION_JSON);
        }
    }
}

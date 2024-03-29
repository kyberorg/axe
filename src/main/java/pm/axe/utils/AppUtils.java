package pm.axe.utils;

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
import kong.unirest.MimeTypes;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import pm.axe.Axe;
import pm.axe.internal.AxeGsonExclusionStrategy;
import pm.axe.session.AxeSession;

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

    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ssZ";
    @Getter
    private final Environment env;

    /**
     * This field is dirty hack to access{@link #env} from static context.
     * To be populated with {@link #populateStaticFields()}
     */
    @Getter
    private static Environment appEnv;

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

    /**
     * This field is dirty hack to access Facebook App ID from static context.
     * To be populated with {@link #populateStaticFields()}
     */
    @Getter
    private static String facebookId;

    /**
     * This field is dirty hack to access Telegram Bot Name from static context.
     * To be populated with {@link #populateStaticFields()}
     */
    @Getter
    private static String telegramBotName;

    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setDateFormat(DATE_FORMAT)
            .addSerializationExclusionStrategy(AxeGsonExclusionStrategy.get())
            .create();

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
        appEnv = env;
        shortUrl = getShortUrl();
        sessionTimeout = getSessionTimeout();
        facebookId = getFacebookAppId();
        telegramBotName = getTelegramBotsName();
    }

    /**
     * Defines if {@literal default} Spring profile is activated.
     * Since {@literal default} ain't appear within {@link Environment#getActiveProfiles()} method's logic is complex.
     * This method ignores non-environmental profiles.
     *
     * @return true - if not another environmental profile is active, false - if app runs with another profile.
     */
    public static boolean isDefaultProfileActive() {
        String[] activeProfiles = getAppEnv().getActiveProfiles();
        if (activeProfiles.length == 0) return true;
        boolean isProfileNotEnvironmental;
        boolean anotherEnvProfileFound = false;
        for (String profile: activeProfiles) {
            isProfileNotEnvironmental = (profile.equals(Axe.Profiles.ACTUATOR) || profile.equals(Axe.Profiles.PROXY));
            if (!isProfileNotEnvironmental) {
                anotherEnvProfileFound = true;
                break;
            }
        }
        return !anotherEnvProfileFound;
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
     * @return true, if clients {@link Axe.Headers#ACCEPT} contains {@link MimeTypes#JSON} mime type,
     * false - elsewhere
     */
    public static boolean clientWantsJson(final HttpServletRequest request) {
        String acceptHeader = request.getHeader(Axe.Headers.ACCEPT);
        return clientWantsJson(acceptHeader);
    }

    /**
     * Determines if client wants to receive JSON from us. Vaadin's implementation.
     *
     * @param vaadinRequest valid {@link VaadinRequest} from VaadinServlet
     * @return true, if clients {@link Axe.Headers#ACCEPT} contains {@link MimeTypes#JSON} mime type,
     * false - elsewhere
     */
    public static boolean clientWantsJson(final VaadinRequest vaadinRequest) {
        String acceptHeader = vaadinRequest.getHeader(Axe.Headers.ACCEPT);
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
     * Determines if client's request has {@link Axe.Headers#ACCEPT} header with exact MIME-type.
     *
     * @param req valid {@link HttpServletRequest} request
     * @return true, if request has {@link Axe.Headers#ACCEPT} header and its value is not wildcard
     * (aka accept all) {@link Axe.C#ALL_MIME_TYPES}, false - elsewhere
     */
    public static boolean hasAcceptHeader(final HttpServletRequest req) {
        boolean acceptHeaderPresent = StringUtils.isNotBlank(req.getHeader(Axe.Headers.ACCEPT));
        if (acceptHeaderPresent) {
            @SuppressWarnings("UnnecessaryLocalVariable") //increase readability
            boolean hasExactMimeType = !req.getHeader(Axe.Headers.ACCEPT).equals(Axe.C.ALL_MIME_TYPES);
            return hasExactMimeType;
        } else {
            return false;
        }
    }

    /**
     * This is needed to handle shit like {@literal text/html,application/xhtml+xml,application/xml;q=0.9,**;q=0.8}.
     *
     * @param req request
     * @return true - any part Accept Header contains {@link MimeTypes#HTML}, false elsewhere
     */
    public static boolean clientWantsHtml(final HttpServletRequest req) {
        boolean acceptHeaderPresent = StringUtils.isNotBlank(req.getHeader(Axe.Headers.ACCEPT));
        if (!acceptHeaderPresent) {
            return false;
        }
        String rawAcceptHeader = req.getHeader(Axe.Headers.ACCEPT);
        String[] parts = rawAcceptHeader.split(",");
        if (parts.length < 1) {
            return false;
        }
        for (String mimeType : parts) {
            if (mimeType.equals(MimeTypes.HTML)) {
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
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        Label label = new Label(text);
        label.getStyle().set("margin-right", "0.5rem");
        Button closeButton = new Button("OK", event -> notification.close());
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.getStyle().set("margin-right", "0.5rem");

        layout.add(label, closeButton);
        notification.add(layout);
        return notification;
    }

    /**
     * Creates Session Expired Notification.
     *
     * @param ui      non-empty {@link UI} to refresh page.
     * @param session session to get bound device information and if it is mobile - adjust notification accordingly.
     * @return created {@link Notification}.
     */
    public static Notification getSessionExpiredNotification(final UI ui, final AxeSession session) {
        if (ui == null) throw new IllegalArgumentException("UI cannot be null");
        if (session == null) throw new IllegalArgumentException("session cannot be null");
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        notification.setPosition(Notification.Position.TOP_STRETCH);

        boolean isMobileDevice = session.hasDevice() && session.getDevice().isMobile();

        String message;
        if (isMobileDevice) {
            message = "Session expires soon. Any unsaved data will be lost.";
        } else {
            message = String.format("Your session expires in %d minutes. Take note of any unsaved data.",
                    AxeSession.TIMEOUT_FOR_WARNING_MINUTES);
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
        String serverUrl = env.getProperty(Axe.Properties.SERVER_URL);
        return StringUtils.isNotBlank(serverUrl) ? serverUrl : DUMMY_HOST;
    }

    /**
     * Provides short url used for links. Runtime value.
     *
     * @return string with short url (scheme + short domain) if found or Server Url from {@link #getServerUrl()}
     */
    public String getShortUrl() {
        String shortDomain = env.getProperty(Axe.Properties.SHORT_DOMAIN, DUMMY_HOST);
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
        String serverUrlWithoutProto = UrlUtils.removeProtocol(getServerUrl());
        //fix for local instance
        if (serverUrlWithoutProto.contains(":")) {
            serverUrlWithoutProto = serverUrlWithoutProto.split(":")[0];
        }
        return serverUrlWithoutProto;
    }

    /**
     * Provides host of short url used for links. Runtime value.
     *
     * @return string with short domain, if found or Server Url from {@link #getServerUrl()}
     */
    public String getShortDomain() {
        String shortDomain = env.getProperty(Axe.Properties.SHORT_DOMAIN, DUMMY_HOST);
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
        String token = env.getProperty(Axe.Properties.TELEGRAM_TOKEN);
        return StringUtils.isNotBlank(token) ? token : DUMMY_TOKEN;
    }

    /**
     * Defines is telegram integration is enabled or not.
     *
     * @return {@link Axe.Properties#TELEGRAM_ENABLED} property value or false
     */
    public boolean isTelegramDisabled() {
        return !Boolean.parseBoolean(env.getProperty(Axe.Properties.TELEGRAM_ENABLED, "false"));
    }

    /**
     * Defines if application works in Development Mode. Dev Mode enables developer features (i.e. Debug Page)
     *
     * @return true if Dev Mode is activated, else false
     */
    public boolean isDevelopmentModeActivated() {
        return Boolean.parseBoolean(env.getProperty(Axe.Properties.DEV_MODE, "false"));
    }

    /**
     * Checks if {@link Axe.Headers#X_DEVELOPER} header is present.
     *
     * @return true if header is present, false if not.
     */
    public boolean hasDevHeader() {
        String xDeveloper = VaadinRequest.getCurrent().getHeader(Axe.Headers.X_DEVELOPER);
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
     * Allow/Disallow crawlers (search engine bots), based on profile settings.
     *
     * @return true - if crawlers should be allowed, false - elsewhere
     */
    public boolean areCrawlersAllowed() {
        return Boolean.parseBoolean(getEnv().getProperty(Axe.Properties.CRAWLERS_ALLOWED));
    }

    /**
     * Reads redirect page bypass symbol from settings.
     *
     * @return string with skip mark or {@link Axe.C#NO_VALUE}
     */
    public String getRedirectPageBypassSymbol() {
        return getEnv().getProperty(Axe.Properties.REDIRECT_PAGE_BYPASS_SYMBOL, Axe.C.NO_VALUE);
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
     * @return int with timeout from settings or default timeout {@link Axe.Defaults#REDIRECT_PAGE_TIMEOUT_SECONDS}
     */
    public int getRedirectPageTimeout() {
        String timeoutString = getEnv().getProperty(Axe.Properties.REDIRECT_PAGE_TIMEOUT, Axe.C.NO_VALUE);
        if (timeoutString.equals(Axe.C.NO_VALUE)) {
            return Axe.Defaults.REDIRECT_PAGE_TIMEOUT_SECONDS;
        }
        return Integer.parseInt(timeoutString);
    }

    /**
     * Reads Session Timeout from settings.
     *
     * @return int with  timeout from settings or default timeout {@link Axe.Defaults#SESSION_TIMEOUT_SECONDS}
     */
    public int getSessionTimeout() {
        String timeoutString = getEnv().getProperty(Axe.Properties.SESSION_TIMEOUT, Axe.C.NO_VALUE);
        if (timeoutString.equals(Axe.C.NO_VALUE)) {
            return Axe.Defaults.SESSION_TIMEOUT_SECONDS;
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
            return false;
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

    /**
     * Reads Facebook AppId from Settings.
     *
     * @return String with Facebook Application ID or {@link Axe.C#NO_VALUE}.
     */
    public String getFacebookAppId() {
        return getEnv().getProperty(Axe.Properties.FACEBOOK_APP_ID, Axe.C.NO_VALUE);
    }

    /**
     * Reads Email for sending errors from Settings.
     *
     * @return String with Email for sending errors or {@link Axe.C#NO_VALUE}.
     */
    public String getEmailForErrors() {
        return getEnv().getProperty(Axe.Properties.EMAIL_FOR_ERRORS, Axe.C.NO_VALUE);
    }

    /**
     * Reads from profile Email Address we should send letters from.
     *
     * @return String with Email Address or default value.
     */
    public String getEmailFromAddress() {
        return getEnv().getProperty(Axe.Properties.EMAIL_FROM_ADDRESS, Axe.Defaults.EMAIL_FROM_ADDRESS);
    }

    /**
     * Reads Application Name from properties.
     *
     * @return String with Application Name,defined in profile or hardcoded value {@literal Axe}.
     */
    public String getApplicationName() {
        return getEnv().getProperty(Axe.Properties.APPLICATION_NAME, "Axe");
    }

    /**
     * Provides Master Token from EnvVars. This is hardcoded Token to perform potentially destructive operations.
     *
     * @return string with token, or {@link Axe.C#NO_VALUE}.
     */
    public String getMasterToken() {
        return getEnv().getProperty(Axe.Envs.MASTER_TOKEN, Axe.C.NO_VALUE);
    }

    /**
     * Provides Telegram Bot Name from Properties.
     *
     * @return string with bot's name, or {@link Axe.C#NO_VALUE}.
     */
    public String getTelegramBotsName() {
        return getEnv().getProperty(Axe.Properties.TELEGRAM_BOT_NAME, Axe.C.NO_VALUE);
    }

    /**
     * Determines if app should show "Yalsee is now Axe" Notification.
     * Regulated by profile property {@link  Axe.Properties#SHOW_RENAME_NOTIFICATION}
     *
     * @return property value
     */
    public boolean showRenameNotification() {
        String showNotificationString = getEnv().getProperty(Axe.Properties.SHOW_RENAME_NOTIFICATION, "true");
        return Boolean.parseBoolean(showNotificationString);
    }

    private static boolean clientWantsJson(final String acceptHeader) {
        if (acceptHeader == null) {
            return false;
        } else {
            return acceptHeader.equals(MimeTypes.JSON);
        }
    }
}

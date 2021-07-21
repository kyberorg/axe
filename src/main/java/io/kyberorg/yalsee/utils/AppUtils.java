package io.kyberorg.yalsee.utils;

import com.google.common.base.CharMatcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinRequest;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.constants.MimeType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;

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

    /**
     * This field is dirty hack to access Short URL from static context.
     * To be populated with {@link #populateStaticFields()}
     */
    private static String shortUrl;

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

    private void populateStaticFields() {
        shortUrl = getShortUrl();
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
     * @return true - if crawlers should be allow, false - elsewhere
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
     * Drops redirect page bypass symbol from provided ident string. It also check if string has this symbol.
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
        String timeoutString =  getEnv().getProperty(App.Properties.REDIRECT_PAGE_TIMEOUT, App.NO_VALUE);
        if (timeoutString.equals(App.NO_VALUE)) {
            return App.Defaults.REDIRECT_PAGE_TIMEOUT_SECONDS;
        }
        return Integer.parseInt(timeoutString);
    }

    private static boolean clientWantsJson(final String acceptHeader) {
        if (acceptHeader == null) {
            return false;
        } else {
            return acceptHeader.equals(MimeType.APPLICATION_JSON);
        }
    }
}

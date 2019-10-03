package eu.yals.utils;

import com.google.common.base.CharMatcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.yals.constants.App;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.codec.EncodingException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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
    public boolean isAscii(String str) {
        return CharMatcher.ascii().matchesAllOf(str);
    }

    /**
     * Makes fully qualified URI resource from string with url.
     *
     * @param url string with valid URL
     * @return URI from same URL if URL already has schema or URI from default http schema and requested URL
     * @throws IllegalArgumentException if string has not valid URL or not URL
     */
    public URI makeFullUri(String url) {
        try {
            URI uri = new URI(url);

            if (uri.getScheme() == null) {
                uri = new URI("http://" + url);
            }
            return uri;
        } catch (URISyntaxException e) {
            String message = String.format("String '%s': malformed URL or not URL at all", url);
            log.warn(message);
            throw new IllegalArgumentException(message, e);
        }
    }

    /**
     * Code taken from {@link https://nealvs.wordpress.com/2016/01/18/how-to-convert-unicode-url-to-ascii-in-java/}
     *
     * @param rawUrl string with valid URL to convert
     * @return is URL contains only ASCII chars - same URL, otherwise punycoded URL,
     * @throws IllegalArgumentException if URL malformed or not URL
     */
    public String covertUnicodeToAscii(String rawUrl) {
        if (rawUrl != null) {
            String url = rawUrl.trim();

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
                throw new IllegalArgumentException(message, e);
            }
        }
        return rawUrl;
    }

    /**
     * Decodes URL from wiki/%D0%9E%D1%80%D0%B5%D1%81%D1%82 to wiki/Орест
     *
     * @param encodedUrl string with URL where encoded chars are present or not
     * @return string with decoded URL or same string if URL has no chars to encode
     */
    public String decodeUrl(String encodedUrl) {
        try {
            return URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            String message = "Failed to decode URL";
            log.error(message, e);
            throw new EncodingException(message, e.getCause());
        }
    }

    public boolean isTelegramDisabled() {
        return !Boolean.parseBoolean(env.getProperty(App.Properties.TELEGRAM_ENABLED, "false"));
    }
}

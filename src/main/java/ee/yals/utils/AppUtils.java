package ee.yals.utils;

import com.google.common.base.CharMatcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ee.yals.constants.App;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * App-wide tools
 *
 * @since 1.0
 */
@Slf4j
@Component
public class AppUtils {

    private Environment env;

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
     * Code taken from {@link https://nealvs.wordpress.com/2016/01/18/how-to-convert-unicode-url-to-ascii-in-java/}
     *
     * @param url string with valid URL to convert
     * @return is URL contains only ASCII chars - same URL, otherwise punycoded URL,
     * if URL malformed or not URL (string "ERROR" will returned)
     */
    public String covertUnicodeToAscii(String url) {
        if (url != null) {
            url = url.trim();

            // Handle international domains by detecting non-ascii and converting them to punycode
            if (isAscii(url)) return url;

            URI uri;
            try {
                uri = new URI(url);

                boolean includeScheme = true;
                if (uri.getScheme() == null) {
                    uri = new URI("http://" + url);
                    includeScheme = false;
                }

                String scheme = uri.getScheme() != null ? uri.getScheme() + "://" : null;
                String authority = uri.getRawAuthority() != null ? uri.getRawAuthority() : ""; // includes domain and port
                String path = uri.getRawPath() != null ? uri.getRawPath() : "";
                String queryString = uri.getRawQuery() != null ? "?" + uri.getRawQuery() : "";

                // Must convert domain to punycode separately from the path
                url = (includeScheme ? scheme : "") + IDN.toASCII(authority) + path + queryString;
                // Convert path from unicode to ascii encoding
                url = new URI(url).toASCIIString();
            } catch (URISyntaxException e) {
                log.warn("Got malformed URL {}", url);
                return "ERROR";
            }
        }
        return url;
    }

    public boolean isAscii(String str) {
        return CharMatcher.ascii().matchesAllOf(str);
    }
}

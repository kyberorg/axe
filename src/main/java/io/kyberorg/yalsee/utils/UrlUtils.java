package io.kyberorg.yalsee.utils;

import io.kyberorg.yalsee.exception.URLDecodeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static io.kyberorg.yalsee.utils.AppUtils.isAscii;

/**
 * URL-related helper functions.
 *
 * @since 3.1
 */
@Slf4j
public final class UrlUtils {
    private static final String TAG = "[" + UrlUtils.class.getSimpleName() + "]";

    private UrlUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Adds missing schema, if needed and converting link from Unicode to ASCII.
     *
     * @param link string with link to normalize
     * @return string with ASCII-encoded link
     */
    public static String normalizeUrl(final String link) {
        if (StringUtils.isNotBlank(link)) {
            //normalize URL if needed
            try {
                String fullUrl = makeFullUri(link).toString();
                log.trace("{} Link {} became {} after adding schema", TAG, link, fullUrl);
                String convertedUrl = covertUnicodeUrlToAscii(fullUrl);
                log.trace("{} Link {} converted to {}", TAG, fullUrl, convertedUrl);
                return convertedUrl;
            } catch (RuntimeException e) {
                //to be handled by validators
                return link;
            }
        } else {
            return link;
        }
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
    public static String covertUnicodeUrlToAscii(final String url) {
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
     * @throws URLDecodeException thrown when application it failed to decode URL.
     */
    public static String decodeUrl(final String encodedUrl) throws URLDecodeException {
        try {
            return URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("{} Failed to decode URL", TAG);
            log.debug("", e);
            throw new URLDecodeException(e.getCause());
        }
    }

    /**
     * Removes protocol/schema (HTTP/HTTPS/FTP) part from URL string.
     *
     * @param urlWithProtocol string with URL, which contains protocol/schema part.
     * @return string with same URL, but without protocol or same string if string has no protocol.
     */
    public static String removeProtocol(final String urlWithProtocol) {
        return urlWithProtocol.replaceFirst("https", "").replaceFirst("http", "")
                .replaceFirst("ftp", "").replaceFirst("://", "");
    }

    private static String replaceSpacesInUrl(final String originUrl) {
        return originUrl.replaceAll(" ", "+");
    }
}

package io.kyberorg.yalsee.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * URL-related helper functions.
 *
 * @since 3.1
 */
@Slf4j
public class UrlUtils {
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
                String fullUrl = AppUtils.makeFullUri(link).toString();
                log.trace("{} Link {} became {} after adding schema", TAG, link, fullUrl);
                String convertedUrl = AppUtils.covertUnicodeToAscii(fullUrl);
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

}

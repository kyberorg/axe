package eu.yals.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * Does extra validation of URL to filter out not valid URLs passed
 * thru {@link org.hibernate.validator.constraints.URL} validation.
 *
 * @since 2.0
 */
public final class UrlExtraValidator {
    private static final String URL_MARKER = "://";

    private UrlExtraValidator() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String VALID = "VALID";
    public static final String URL_NOT_VALID = "URL is malformed, not URL at all or just protocol not supported yet";
    public static final int URL_MIN_SIZE = 5;
    public static final int URL_MAX_SIZE = 15613;

    /**
     * Defines if given URL is valid or not.
     *
     * @param url string with URL to check
     * @return {@link #VALID} if valid, {@link #URL_NOT_VALID} if not valid
     */
    public static String isUrlValid(final String url) {
        UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
        return validator.isValid(url) ? VALID : URL_NOT_VALID;
    }

    /**
     * Defines if given string is URL or not.
     *
     * @param url string to control
     * @return true is string is valid URL, false if not
     */
    public static boolean isUrl(final String url) {
        UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
        return validator.isValid(url);
    }

    /**
     * Defines if given string contains URL or not.
     *
     * @param string string to control
     * @return true is string contains valid URL, false if not
     */
    public static boolean isStringContainsUrl(final String string) {
        if (StringUtils.isBlank(string)) {
            return false;
        }
        String[] words = string.split(" ");
        int urlCount = 0;
        for (String word : words) {
            if (word.contains(URL_MARKER) && new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS).isValid(word)) {
                urlCount += 1;
            }
        }
        return urlCount > 0;
    }
}

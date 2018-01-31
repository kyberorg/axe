package ee.yals.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * Does extra validation of URL to filter out not valid URLs passed thru {@link org.hibernate.validator.constraints.URL} validation
 *
 * @since 2.0
 */
public class UrlExtraValidator {
    private static final String URL_MARKER = "://";
    private UrlExtraValidator(){
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String VALID = "VALID";
    public static final String URL_NOT_VALID = "URL is malformed, not URL at all or just protocol not supported yet";

    public static String isUrlValid(String url) {
        UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
        return validator.isValid(url) ? VALID : URL_NOT_VALID;
    }

    public static boolean isStringContainsUrl(String string) {
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

package ee.yals.utils;

import org.apache.commons.validator.routines.UrlValidator;

/**
 * Does extra validation of URL
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public class UrlExtraValidator {
    private UrlExtraValidator(){
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String VALID = "VALID";
    public static final String URL_NOT_VALID = "URL is malformed, not URL at all or just protocol not supported yet";

    public static String isUrlValid(String url) {
        UrlValidator validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
        return validator.isValid(url) ? VALID : URL_NOT_VALID;
    }

}

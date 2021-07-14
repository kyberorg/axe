package io.kyberorg.yalsee.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class URLUtils {
    private static final String TAG = "[" + URLUtils.class.getSimpleName() + "]";

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

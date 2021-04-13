package io.kyberorg.yalsee.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks if link should be banned from storing.
 *
 * @since 3.0.5
 */
public final class BanHammer {
    private static final List<String> BAN_LIST = new ArrayList<>();

    static {
        // @see issue #350
        BAN_LIST.add("tmweb.ru");
    }

    private BanHammer() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Examines URL on ban list.
     *
     * @param url string with URL to check
     * @return true if URL or its domain in ban list, false - if not
     */
    public static boolean shouldBeBanned(final String url) {
        boolean belongsToBannedDomain = false;
        for (String item : BAN_LIST) {
            if (url.contains(item)) {
                belongsToBannedDomain = true;
                break;
            }
        }
        return belongsToBannedDomain;
    }
}

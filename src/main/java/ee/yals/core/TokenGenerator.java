package ee.yals.core;

import org.apache.commons.lang3.RandomStringUtils;

public class TokenGenerator {
    private TokenGenerator() {
    }
    public static String generateNew() {
        return RandomStringUtils.randomAlphanumeric(20);
    }
}

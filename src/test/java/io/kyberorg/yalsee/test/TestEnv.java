package io.kyberorg.yalsee.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Provides information about Environment we test.
 *
 * @since 3.0.2
 */
@AllArgsConstructor
public enum TestEnv {
    LOCAL("http://localhost:8080", "http://l.yls.ee", false, "-"),
    DEV("https://dev.yals.ee", "https://d.yls.ee", true, "gtag.dev.html"),
    DEMO("https://demo.yals.ee", "https://q.yls.ee", true, "gtag.demo.html"),
    PROD("https://yals.ee", "https://yls.ee", true, "gtag.html");

    @Getter private final String testUrl;
    @Getter private final String shortUrl;
    @Getter private final boolean googleAnalyticsEnabled;
    @Getter private final String googleAnalyticsFileName;

    /**
     * Gets {@link TestEnv} by its url.
     *
     * @param envUrl string with URL should be like https://site.tld
     * @return {@link TestEnv} object for matched env or {@link #LOCAL} if no match found
     */
    public static TestEnv getByTestUrl(final String envUrl) {
        for (TestEnv env: TestEnv.values()) {
           if (env.getTestUrl().equals(envUrl)) {
               return env;
            }
        }
        return LOCAL;
    }

}

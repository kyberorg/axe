package io.kyberorg.yalsee.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Provides information about Environment we test
 */
@AllArgsConstructor
public enum TestEnv {
    LOCAL("http://localhost:8080", "http://l.yls.ee",false,"-"),
    DEV("https://dev.yals.ee","https://d.yls.ee",true,"gtag.dev.html"),
    DEMO("https://demo.yals.ee","https://q.yls.ee",true, "gtag.demo.html"),
    PROD("https://yals.ee","https://yls.ee",true,"gtag.html");

    @Getter private final String testUrl;
    @Getter private final String shortUrl;
    @Getter private final boolean googleAnalyticsEnabled;
    @Getter private final String googleAnalyticsFileName;

    public static TestEnv getByTestUrl(String testUrl) {
        for (TestEnv env: TestEnv.values()) {
           if (env.getTestUrl().equals(testUrl)) {
               return env;
            }
        }
        return LOCAL;
    }

}

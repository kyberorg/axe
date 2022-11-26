package pm.axe.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.net.URI;

/**
 * Provides information about Environment we test.
 *
 * @since 3.0.2
 */
@AllArgsConstructor
public enum TestedEnv {
    LOCAL("http://localhost:8080", "http://l.axe.pm", "+"),
    DEV("https://dev.axe.pm", "https://d.axe.pm", "+"),
    POC("https://poc.axe.pm", "https://p.axe.pm",  "+"),
    QA("https://qa.axe.pm", "https://q.axe.pm",  "+"),
    PROD("https://axe.pm", "https://axe.pm",  "+");

    @Getter
    private final String testUrl;
    @Getter
    private final String shortUrl;
    @Getter
    private final String redirectPageBypassSymbol;

    /**
     * Gets {@link TestedEnv} by its url.
     *
     * @param envUrl string with URL should be like <a href="https://site.tld">https://site.tld</a>
     * @return {@link TestedEnv} object for matched env or {@link #LOCAL} if no match found
     */
    public static TestedEnv getByTestUrl(final String envUrl) {
        for (TestedEnv env : TestedEnv.values()) {
            if (env.getTestUrl().equals(envUrl)) {
                return env;
            }
        }
        return LOCAL;
    }

    /**
     * Gets Host (Domain) name of {@link TestedEnv}. Same as {@link #testUrl}, but only hostname i.e. axe.pm.
     *
     * @return string with hostname
     */
    public String getTestHost() {
        return extractHostFrom(testUrl);
    }

    /**
     * Gets Short Host (Short Domain) name of {@link TestedEnv}.
     * Same as {@link #shortUrl}, but only hostname i.e. axe.pm.
     *
     * @return string with short domain
     */
    public String getShortHost() {
        return extractHostFrom(shortUrl);
    }

    @SneakyThrows
    private String extractHostFrom(final String url) {
        URI uri = new URI(url);
        return uri.getHost();
    }
}

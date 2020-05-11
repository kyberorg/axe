package eu.yals.configuration;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagSpringConfiguration;
import eu.yals.constants.App;
import eu.yals.utils.git.MavenGitInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Configures Bugsnag integration.
 *
 * @since 2.7
 */
@Slf4j
@Configuration
@Import(BugsnagSpringConfiguration.class)
public class BugsnagConfig {
    private static final String TAG = "[" + BugsnagConfig.class.getSimpleName() + "]";
    private static final String NO_TOKEN = "noToken";

    private final Environment env;
    private final MavenGitInfo mavenGitInfo;

    private String proxyHost;
    private String proxyPort;
    private Bugsnag bugsnag;

    /**
     * Constructor for Spring autowiring.
     *
     * @param environment {@link Environment} for accessing to env vars
     * @param gitInfo     for getting application version from pom
     */
    public BugsnagConfig(final Environment environment, final MavenGitInfo gitInfo) {
        this.env = environment;
        this.mavenGitInfo = gitInfo;
    }

    /**
     * Configures connection to Bugsnag services.
     *
     * @return configured bean, which handles connections/sessions/API calls.
     */
    @Bean
    public Bugsnag bugsnag() {
        String bugsnagToken = env.getProperty(App.Env.BUGSNAG_TOKEN, NO_TOKEN);
        log.info("{} Initialing Bugsnag with token {}", TAG, bugsnagToken);
        bugsnag = new Bugsnag(bugsnagToken);
        if (hasProxy()) {
            setProxy();
        }
        bugsnag.setAppType("maven");
        if (mavenGitInfo.isApplicable()) {
            bugsnag.setAppVersion(mavenGitInfo.getLatestTag());
        }
        setApplicationStage();
        return bugsnag;
    }

    private boolean hasProxy() {
        this.proxyHost = env.getProperty(App.Properties.PROXY_HOST, "");
        this.proxyPort = env.getProperty(App.Properties.PROXY_PORT, "");

        return (StringUtils.isNotBlank(this.proxyHost) && (StringUtils.isNotBlank(this.proxyPort)));
    }

    private void setProxy() {
        bugsnag.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort))));
    }

    private void setApplicationStage() {
        String stage = env.getProperty(App.Properties.APPLICATION_STAGE, "unknown");
        bugsnag.setReleaseStage(stage);
    }
}

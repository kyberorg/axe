package eu.yals.configuration;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagSpringConfiguration;
import eu.yals.constants.App;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
@Import(BugsnagSpringConfiguration.class)
public class BugsnagConfig {
    private static final String NO_TOKEN = "noToken";

    private final Environment env;

    public BugsnagConfig(Environment env) {
        this.env = env;
    }

    private String proxyHost;
    private String proxyPort;
    private Bugsnag bugsnag;

    @Bean
    public Bugsnag bugsnag() {
        String bugsnagToken = env.getProperty(App.Env.BUGSNAG_TOKEN, NO_TOKEN);
        bugsnag = new Bugsnag(bugsnagToken);
        if (hasProxy()) {
            setProxy();
        }
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
}

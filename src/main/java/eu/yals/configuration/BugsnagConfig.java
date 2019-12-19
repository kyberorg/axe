package eu.yals.configuration;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagSpringConfiguration;
import eu.yals.constants.App;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Configuration
@Import(BugsnagSpringConfiguration.class)
public class BugsnagConfig {
    private static final String NO_TOKEN = "noToken";

    private final Environment env;

    public BugsnagConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public Bugsnag bugsnag() {
        String bugsnagToken = env.getProperty(App.Env.BUGSNAG_TOKEN, NO_TOKEN);
        return new Bugsnag(bugsnagToken);
    }
}

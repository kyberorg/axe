package ee.yals.utils.git;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Provides git information (maven way)
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 2.0
 */
@Component
public class MavenGitInfo extends GitInfo {
    /**
     * No reason for instance. Use {@link GitInfo#getInstance()} instead
     */
    MavenGitInfo() { }

    @Override
    boolean isApplicable() {
        //TODO implement
        return true;
    }

    @Override
    public String getLatestCommitHash() {
        //TODO implement
        PropertySourcesPlaceholderConfigurer props = placeholderConfigurer();
        return props.toString();
    }

    @Override
    public String getLatestTag() {
        //TODO implement
        return GitInfo.NOTHING_FOUND_MARKER;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propsConfig
                = new PropertySourcesPlaceholderConfigurer();
        propsConfig.setLocation(new ClassPathResource("git.properties"));
        propsConfig.setIgnoreResourceNotFound(true);
        propsConfig.setIgnoreUnresolvablePlaceholders(true);
        return propsConfig;
    }
}

package pm.axe.utils.maven;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Object with properties from pom.xml created at build time.
 * <p>
 * java.version
 * vaadin.version
 * selenium.version
 *
 * @since 2.7
 */
@Slf4j
@Data
@Component
public class MavenInfo {
    private static final String TAG = "[" + MavenInfo.class.getSimpleName() + "]";
    private static final String MAVEN_PROPERTIES_FILE = "maven.properties";

    private final Properties mvnProperties = new Properties();

    private String vaadinVersion; //vaadin.version

    /**
     * Creates {@link MavenInfo} object.
     */
    public MavenInfo() {
        init();
    }

    /**
     * Defines if object populated values or not.
     *
     * @return true object correctly populated, false if not.
     */
    public boolean hasValues() {
        return !mvnProperties.isEmpty();
    }

    private void init() {
        if (this.getClass().getClassLoader() == null) {
            log.error("{} '{}': no such file. Did you run 'mvn package' ? (Note: ignore, if profile is 'default')",
                    TAG, MAVEN_PROPERTIES_FILE);
            this.mvnProperties.clear();
            return;
        }
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(MAVEN_PROPERTIES_FILE);
        if (is == null) {
            log.warn("{} '{}': no such file. Did you run 'mvn package' ? (Note: ignore, if profile is 'default')",
                    TAG, MAVEN_PROPERTIES_FILE);
            this.mvnProperties.clear();
            return;
        }
        try {
            mvnProperties.load(is);
            this.publishFromProperties();
        } catch (IOException e) {
            log.error("{} Failed to init " + MavenInfo.class.getSimpleName(), TAG);
            log.debug("", e);
            this.mvnProperties.clear();
        }
    }

    private void publishFromProperties() {
        this.vaadinVersion = String.valueOf(mvnProperties.get("vaadin.version"));
    }
}

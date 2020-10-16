package eu.yals;

import co.elastic.apm.attach.ElasticApmAttacher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application Start point.
 */
@SpringBootApplication
public class YalsApplication {

    /**
     * Main class.
     *
     * @param args standard signature
     */
    public static void main(final String[] args) {
        ElasticApmAttacher.attach();
        SpringApplication.run(YalsApplication.class, args);
    }
}

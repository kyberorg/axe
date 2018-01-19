package ee.yals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main (Start point)
 */
@SpringBootApplication
public class YalsApplication {

    /**
     * Default application user when exact user is not set
     */
    public static final String YALS_GOD = "yalsGod";

    public static void main(String[] args) {
        SpringApplication.run(YalsApplication.class, args);
    }
}

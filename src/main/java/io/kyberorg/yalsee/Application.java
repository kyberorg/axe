package io.kyberorg.yalsee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

/**
 * Application Start point.
 */
//https://vaadin.com/forum/thread/17784869/vaadin-14-with-spring-security-login-page-not-loading
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class Application {

    /**
     * Main class.
     *
     * @param args standard signature
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

package eu.yals.configuration;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

/**
 * Mapping Error Statuses to Error Pages
 *
 * @since 2.5
 */
@Component
public class ErrorHandlingConfiguration implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
       /* factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, Endpoint.NOT_FOUND_PAGE));
        factory.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, Endpoint.ERROR_PAGE));
        factory.addErrorPages(new ErrorPage(Endpoint.ERROR_PAGE));*/
    }
}
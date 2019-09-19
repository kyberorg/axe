package ee.yals.configuration;

import ee.yals.Endpoint;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ErrorHandlingConfiguration implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, Endpoint.NOT_FOUND));
        factory.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, Endpoint.ERROR_PAGE));
        factory.addErrorPages(new ErrorPage(Endpoint.ERROR_PAGE));
    }
}
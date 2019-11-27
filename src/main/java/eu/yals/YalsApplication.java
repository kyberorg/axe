package eu.yals;

import com.vaadin.flow.server.VaadinServlet;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.json.ErrorJson;
import eu.yals.utils.AppUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Main (Start point)
 */
@SpringBootApplication
public class YalsApplication {

    public static void main(String[] args) {
        SpringApplication.run(YalsApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean notFoundServletBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean<>(new VaadinServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                boolean hasAcceptHeader = AppUtils.hasAcceptHeader(req);
                if (hasAcceptHeader && !AppUtils.clientWantsJson(req)) {
                    resp.setHeader(Header.ACCEPT, MimeType.APPLICATION_JSON);
                    resp.setStatus(406);
                    return;
                }
                resp.setStatus(404);
                resp.setContentType(MimeType.APPLICATION_JSON);
                resp.getWriter().write(ErrorJson.createWithMessage("Endpoint not found").toString());
            }
        }, Endpoint.Api.PAGE_404);
        bean.setLoadOnStartup(1);
        return bean;
    }
}

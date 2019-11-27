package eu.yals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main (Start point)
 */
@SpringBootApplication
public class YalsApplication {

    public static void main(String[] args) {
        SpringApplication.run(YalsApplication.class, args);
    }

    /*@Bean
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
    }*/
}

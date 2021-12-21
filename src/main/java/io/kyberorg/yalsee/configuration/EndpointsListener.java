package io.kyberorg.yalsee.configuration;

import io.kyberorg.yalsee.utils.AppUtils;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;

import static io.kyberorg.yalsee.constants.HttpCode.STATUS_404;

@RequiredArgsConstructor
@Component
public class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {
    private final AppUtils appUtils;

    private final List<String> routes = new ArrayList<>();

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods().forEach(
                (key, value) -> routes.addAll(key.getDirectPaths())
        );
    }

    public boolean isRouteExists(final String routeToCheck) {
        for (String route : routes) {
            if (StringUtils.isBlank(route)) {
                continue;
            }
            if (route.equals(routeToCheck)) {
                return true;
            }
        }
        //noinspection rawtypes we need only status
        HttpResponse httpResponse = Unirest.get(appUtils.getServerUrl() + routeToCheck).asEmpty();
        return httpResponse.getStatus() != STATUS_404;
    }
}

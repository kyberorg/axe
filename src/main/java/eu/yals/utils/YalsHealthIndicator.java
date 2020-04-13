package eu.yals.utils;

import eu.yals.Endpoint;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Monitors if start page is accessible to users
 *
 * @since 2.7
 */
@Component
public class YalsHealthIndicator implements HealthIndicator {

    private AppUtils appUtils;
    private boolean appStarted = false;

    public YalsHealthIndicator(AppUtils appUtils) {
        this.appUtils = appUtils;
    }

    @Override
    public Health health() {
        if(appStarted) {
            StartPageStatus startPageStatus = isMainPageAccessible();
            switch (startPageStatus) {
                case OK:
                    return Health.up().build();
                case DOWN:
                    return Health.down().outOfService().build();
                case UNKNOWN:
                default:
                    return Health.unknown().build();
            }
        } else {
            return Health.up().build();
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void detectApplicationStartup() {
        appStarted = true;
    }

    private StartPageStatus isMainPageAccessible() {
        if (appUtils == null) {
            return StartPageStatus.UNKNOWN;
        }
        String selfUrl = appUtils.getServerUrl();
        GetRequest request = Unirest.get(selfUrl + Endpoint.UI.HOME_PAGE);
        HttpResponse<String> response = request.asString();
        boolean hasBody = StringUtils.isNotBlank(response.getBody());
        return (response.getStatus() == 200 && hasBody) ? StartPageStatus.OK : StartPageStatus.DOWN;
    }

    public enum StartPageStatus {
        OK,
        DOWN,
        UNKNOWN
    }
}

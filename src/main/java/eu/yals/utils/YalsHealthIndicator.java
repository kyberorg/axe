package eu.yals.utils;

import eu.yals.Endpoint;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class YalsHealthIndicator implements HealthIndicator {

    private AppUtils appUtils;

    public YalsHealthIndicator(AppUtils appUtils) {
        this.appUtils = appUtils;
    }

    @Override
    public Health health() {
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

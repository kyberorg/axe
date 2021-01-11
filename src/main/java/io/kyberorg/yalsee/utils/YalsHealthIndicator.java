package io.kyberorg.yalsee.utils;

import io.kyberorg.yalsee.Endpoint;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static io.kyberorg.yalsee.constants.HttpCode.STATUS_200;

/**
 * Monitors if start page is accessible to users.
 *
 * @since 2.7
 */
@Component
public class YalsHealthIndicator implements HealthIndicator {

    private final AppUtils appUtils;
    private boolean appStarted = false;

    /**
     * Creates {@link YalsHealthIndicator}.
     *
     * @param appUtils application utils
     */
    public YalsHealthIndicator(final AppUtils appUtils) {
        this.appUtils = appUtils;
    }

    /**
     * Detects if application started or not.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void detectApplicationStartup() {
        appStarted = true;
    }

    @Override
    public Health health() {
        if (!appStarted) {
            return Health.up().build();
        }

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
        return (response.getStatus() == STATUS_200 && hasBody) ? StartPageStatus.OK : StartPageStatus.DOWN;
    }

    enum StartPageStatus {
        OK,
        DOWN,
        UNKNOWN
    }
}

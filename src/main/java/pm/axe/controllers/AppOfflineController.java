package pm.axe.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import pm.axe.Endpoint;

/**
 * Simple analog application offline page, which is aka page 503.
 * Needed because Page503 from Vaadin cannot be shown, when memory is low
 *
 * @since 2.7
 */
@Controller
public class AppOfflineController {
    /**
     * Endpoint, which shows application offline page aka page 503.
     *
     * @return string with path to static resource, contains page 503
     */
    @SuppressWarnings("SameReturnValue") //serving static HTML page
    @RequestMapping(Endpoint.TNT.APP_OFFLINE)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String serveAppOfflinePage() {
        return Endpoint.Static.APP_OFFLINE_PAGE;
    }
}

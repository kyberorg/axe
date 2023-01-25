package pm.axe.users;

import lombok.Getter;
import pm.axe.Endpoint;

/**
 * Page, that will open right after successful login.
 */
public enum LandingPage {
    HOME_PAGE(Endpoint.UI.HOME_PAGE),
    MY_LINKS_PAGE(Endpoint.UI.MY_LINKS_PAGE);

    @Getter
    private final String path;

    LandingPage(final String path) {
        this.path = path;
    }
}

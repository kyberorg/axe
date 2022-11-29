package pm.axe.internal;

import lombok.Builder;
import lombok.Data;

/**
 * Matomo (ex. Piwik) configuration.
 */
@Builder
@Data
public class Piwik {
    private final boolean enabled;
    private String piwikHost;
    private String siteId;
}

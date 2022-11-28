package pm.axe.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pm.axe.constants.App;
import pm.axe.internal.Piwik;

@Configuration
public class PiwikConfig {

    @Value(App.Properties.PIWIK_ENABLED)
    private boolean piwikEnabled;

    @Value(App.Properties.PIWIK_HOSTNAME)
    private String piwikHost;

    @Value(App.Properties.PIWIK_SITE_ID)
    private String piwikSiteId;

    /**
     * Creates Piwik Configuration based on properties defined.
     *
     * @return created {@link Piwik} configuration.
     */
    @Bean
    public Piwik piwik() {
        boolean piwikHostIsValid = StringUtils.isNotBlank(piwikHost) && !piwikHost.equals(App.MINUS);
        boolean piwikSiteIdIsValid = StringUtils.isNotBlank(piwikSiteId) && !piwikSiteId.equals(App.MINUS);
        Piwik piwik;
        if (piwikEnabled && piwikHostIsValid && piwikSiteIdIsValid) {
            piwik = Piwik.builder().enabled(true).piwikHost(piwikHost).siteId(piwikSiteId).build();
        } else {
            piwik = Piwik.builder().enabled(false).build();
        }
        return piwik;
    }
}

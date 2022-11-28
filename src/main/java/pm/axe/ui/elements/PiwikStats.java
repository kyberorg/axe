package pm.axe.ui.elements;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.page.Page;
import pm.axe.internal.Piwik;

/**
 * Piwik Stats element.
 */
@Tag("noscript")
public class PiwikStats extends Component implements HasComponents {
    private final Piwik piwik;
    private final Page page;

    public PiwikStats(final Piwik piwik, final Page page) {
        this.piwik = piwik;
        this.page = page;
        if (piwik.isEnabled()) {
            init();
        } //else returning empty component.
    }
    private void init() {
        Paragraph p = new Paragraph();
        Image image = new Image();

        String src = String.format("https://%s/matomo.php?idsite=%s&amp;rec=1",
                piwik.getPiwikHost(), piwik.getSiteId());
        image.setSrc(src);
        image.getStyle().set("border", "0");
        image.setAlt("");

        p.add(image);
        add(p);
    }

    /**
     * Enables tracking.
     */
    public void enableStats() {
        page.executeJs("window.axePiwik($0,$1)", piwik.getPiwikHost(), piwik.getSiteId());
    }

    /**
     * Allowing user to opt-out or opt-in at runtime. {@code false} means opt-in.
     */
    public void optOut(final boolean optOut) {
        page.executeJs("window.axePiwikOptSwitch($0)", optOut);
    }
}

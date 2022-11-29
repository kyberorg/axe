package pm.axe.ui.elements;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Page;
import pm.axe.Endpoint;
import pm.axe.internal.Piwik;
import pm.axe.ui.MainView;
import pm.axe.utils.DeviceUtils;

/**
 * Piwik Stats element.
 */
public class PiwikStats extends Composite<HorizontalLayout> {
    private final Piwik piwik;
    private final MainView mainView;

    private final Page page;

    private final Div leftDiv = new Div();
    private final HorizontalLayout centralLayout = new HorizontalLayout();
    private final Div rightDiv = new Div();

    /**
     * Creates {@link PiwikStats}.
     *
     * @param piwik {@link Piwik} configuration bean.
     * @param mainView {@link MainView} bean to {@link MainView#closeAnnouncementLine()}.
     */
    public PiwikStats(final Piwik piwik, final MainView mainView) {
        this.piwik = piwik;
        this.mainView = mainView;
        this.page = mainView.getUi().getPage();
        if (piwik.isEnabled()) {
            init();
        } //else returning empty component.
    }
    private void init() {
        leftDiv.addClassName("responsive-div");
        centralLayout.addClassName("responsive-center");
        rightDiv.addClassName("responsive-div");

        getContent().add(leftDiv, centralLayout, rightDiv);
        getContent().setWidthFull();

        Icon infoIcon = VaadinIcon.INFO_CIRCLE_O.create();
        Span text = new Span("Axe collects usage statistics");
        Anchor moreInfoLink = new Anchor(Endpoint.UI.APP_INFO_PAGE, "More Info and OptOut");
        Button closeButton = new Button(new Icon("lumo", "cross"));

        centralLayout.add(infoIcon, text, moreInfoLink, closeButton);
        centralLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        centralLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        moreInfoLink.getElement().addEventListener("click", e -> mainView.closeAnnouncementLine());


        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.getStyle().set("margin-right", "0.5rem");
        closeButton.addClickListener(e -> mainView.closeAnnouncementLine());

        final boolean isMobile = DeviceUtils.isMobileDevice();
        if (isMobile) {
            //mobile optimizations
            infoIcon.setVisible(false);
            moreInfoLink.setText("Info and OptOut");
        } else {
            //desktop optimization
            getContent().getStyle().set("margin-left", "3rem");
        }
    }

    /**
     * Enables tracking.
     */
    public void enableStats() {
        page.executeJs("window.axePiwik($0,$1)", piwik.getPiwikHost(), piwik.getSiteId());
    }

    /**
     * Allowing user to opt-out or opt-in at runtime. {@code false} means opt-in.
     *
     * @param optOut true for optOut, false for optIn
     */
    public void optOut(final boolean optOut) {
        page.executeJs("window.axePiwikOptSwitch($0)", optOut);
    }

    /**
     * Has {@link PiwikStats} any content or not?
     *
     * @return true - if {@link PiwikStats} contains at least 1 child, false - if not.
     */
    public boolean isNotEmpty() {
        return getContent().getComponentCount() > 0;
    }

}

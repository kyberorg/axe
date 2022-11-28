package pm.axe.ui.elements;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Page;
import pm.axe.Endpoint;
import pm.axe.internal.Piwik;
import pm.axe.ui.MainView;
import pm.axe.utils.DeviceUtils;

import static pm.axe.constants.App.ONE_SECOND_IN_MILLIS;

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

    private final Notification optOutNotification = makeOptOutNotification();

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
        getContent().getStyle().set("margin-left", "1rem");

        Icon infoIcon = VaadinIcon.INFO_CIRCLE_O.create();
        Span text = new Span("Axe collects usage statistics");
        Anchor moreInfoLink = new Anchor(Endpoint.UI.APP_INFO_PAGE, "More Info");
        Button optOutButton = new Button("OptOut");
        Button closeButton = new Button(new Icon("lumo", "cross"));

        centralLayout.add(infoIcon, text, moreInfoLink, optOutButton, closeButton);
        centralLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        centralLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        moreInfoLink.getElement().addEventListener("click", e -> mainView.closeAnnouncementLine());

        optOutButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        optOutButton.addClickListener(e -> {
            optOut(true);
            optOutNotification.open();
            mainView.closeAnnouncementLine();
        });

        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.getStyle().set("margin-right", "0.5rem");
        closeButton.addClickListener(e -> mainView.closeAnnouncementLine());

        //mobile optimizations
        final boolean isMobile = DeviceUtils.isMobileDevice();
        if (isMobile) {
            infoIcon.setVisible(false);
            moreInfoLink.setText("Info");
            optOutButton.setMinWidth("auto");
        }
        adjustNotificationPosition(isMobile);
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

    /**
     * Has {@link PiwikStats} any content or not?
     *
     * @return true - if {@link PiwikStats} contains at least 1 child, false - if not.
     */
    public boolean isNotEmpty() {
        return getContent().getComponentCount() > 0;
    }

    private Notification makeOptOutNotification() {
        Notification notification = new Notification("Opted out");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(ONE_SECOND_IN_MILLIS); //1 second
        return notification;
    }

    private void adjustNotificationPosition(final boolean isMobile) {
        Notification.Position position = isMobile ? Notification.Position.BOTTOM_CENTER : Notification.Position.MIDDLE;
        this.optOutNotification.setPosition(position);
    }
}

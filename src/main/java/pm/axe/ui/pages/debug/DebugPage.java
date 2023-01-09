package pm.axe.ui.pages.debug;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.services.AxeSessionService;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.elements.CopyToClipboardIcon;
import pm.axe.ui.layouts.AxeBaseLayout;
import pm.axe.utils.AppUtils;

import javax.servlet.http.Cookie;

@RequiredArgsConstructor
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.DEBUG_PAGE, layout = MainView.class)
@PageTitle("Debug Page - Axe.pm")
public class DebugPage extends AxeBaseLayout implements BeforeEnterObserver {
    private final AxeSessionService sessionService;
    private final AppUtils appUtils;
    private final Span axeSessionSpan = new Span();
    private final Span vaadinSessionSpan = new Span();

    private final HorizontalLayout endSessionButtons = new HorizontalLayout();
    private final Button endSessionButton = new Button();
    private final Button endSessionAndCleanCookies = new Button();

    private final Span textSpan = new Span("https://axe.pm");
    private final CopyToClipboardIcon copyToClipboardIcon = new CopyToClipboardIcon();

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        setId(DebugPage.class.getSimpleName());
        removeAll();
        add(new H2("Debug Page"));

        AxeSession.getCurrent().ifPresent(session -> {
            axeSessionSpan.setText("Axe Session ID: " + session.getSessionId());
            add(axeSessionSpan);
        });

        final boolean hasVaadinSession = VaadinSession.getCurrent() != null
                && VaadinSession.getCurrent().getSession() != null
                && VaadinSession.getCurrent().getSession().getId() != null;
        if (hasVaadinSession) {
            vaadinSessionSpan.setText("Vaadin Session ID: " + VaadinSession.getCurrent().getSession().getId());
            add(vaadinSessionSpan);
        }

        add(new Span("Ready to debug something..."));

        endSessionButton.setId("endSessionButton");
        endSessionButtons.add(endSessionButton, endSessionAndCleanCookies);
        add(endSessionButtons);

        endSessionButton.setText("End session");
        endSessionButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        endSessionButton.addClickListener(this::onEndSessionButtonClick);

        endSessionAndCleanCookies.setText("Clean Cookie and End Session");
        endSessionAndCleanCookies.addThemeVariants(ButtonVariant.LUMO_ERROR);
        endSessionAndCleanCookies.addClickListener(this::onCleanCookieEndSessionButtonClick);

        copyToClipboardIcon.getContent().addClickListener(e -> copyToClipboardIcon.setTextToCopy(textSpan.getText()));
        add(textSpan, copyToClipboardIcon);
    }

    private void onEndSessionButtonClick(final ClickEvent<Button> event) {
        cleanSession();
    }

    private void onCleanCookieEndSessionButtonClick(final ClickEvent<Button> event) {
        //clear cookie
        Cookie bannerCookie = new Cookie(Axe.CookieNames.STATS_BANNER_COOKIE, "-");
        bannerCookie.setMaxAge(0);
        VaadinService.getCurrentResponse().addCookie(bannerCookie);

        //clean session
        cleanSession();
    }

    private void cleanSession() {
        AxeSession.getCurrent().ifPresent(sessionService::destroySession);
        appUtils.endVaadinSession(VaadinSession.getCurrent());
    }
}

package pm.axe.ui.pages.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeBaseLayout;

import java.util.stream.Stream;

/**
 * Page, which shown when confirmation performed by {@link ConfirmationView} fails.
 */
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.REGISTRATION_FAILED_PAGE, layout = MainView.class)
@PageTitle("Registration failed - Axe.pm")
public class RegistrationFailedPage extends AxeBaseLayout {
    private final H2 pageTitle = new H2();
    private final Span linkNotValidLine = new Span();
    private final Span expiredLinkLine = new Span();
    private Button loginButton;

    /**
     * Creates {@link RegistrationFailedPage}.
     */
    public RegistrationFailedPage() {
        init();
        applyStyle();

        add(pageTitle, linkNotValidLine, expiredLinkLine, loginButton);
    }

    private void init() {
        pageTitle.setText("Registration failed");

        linkNotValidLine.setText("The confirmation link is no longer valid.");
        expiredLinkLine.setText("It may have been used already or it may have expired.");

        loginButton = new Button("Login", VaadinIcon.SIGN_IN.create());
        loginButton.addClickListener(this::onLoginButtonClicked);
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    private void applyStyle() {
        Stream<? extends HasStyle> elements = Stream.of(pageTitle, linkNotValidLine, expiredLinkLine, loginButton);
        elements.forEach(e -> e.getStyle().set(Axe.Css.ALIGN_SELF, Axe.CssValues.CENTER));
    }

    private void onLoginButtonClicked(final ClickEvent<Button> buttonClickEvent) {
        loginButton.getUI().ifPresent(ui -> ui.navigate(LoginPage.class));
    }
}

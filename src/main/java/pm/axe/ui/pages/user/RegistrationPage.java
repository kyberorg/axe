package pm.axe.ui.pages.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import pm.axe.Endpoint;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeFormLayout;

import java.util.stream.Stream;

@SpringComponent
@UIScope
@RequiredArgsConstructor
@CssImport(value = "./css/registration_page.css")
@Route(value = Endpoint.UI.REGISTRATION_PAGE, layout = MainView.class)
@PageTitle("Registration - Axe.pm")
public class RegistrationPage extends AxeFormLayout implements BeforeEnterObserver {

    private final Span subTitleText = new Span();
    private final Anchor subTitleLink = new Anchor();

    private final FlexLayout userEmailLayout = new FlexLayout();
    private final TextField userEmailInput = new TextField();
    private final Button userEmailInfoButton = new Button(VaadinIcon.INFO_CIRCLE_O.create());

    private final FlexLayout usernameLayout = new FlexLayout();
    private final TextField usernameInput = new TextField();
    private final Button usernameInfoButton = new Button(VaadinIcon.INFO_CIRCLE_O.create());

    private final Details usernameRequirements = new Details();

    private final FlexLayout passwordLayout = new FlexLayout();
    private final PasswordField passwordInput = new PasswordField();
    private final Button passwordInfoButton = new Button(VaadinIcon.INFO_CIRCLE_O.create());

    private final Span tosNote = createLegalInfo();

    private boolean pageAlreadyInitialized = false;

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.isRefreshEvent()) return;
        if (pageAlreadyInitialized) {
            cleanInputs();
        } else {
            pageInit();
            pageAlreadyInitialized = true;
        }
    }
    private void pageInit() {
        setCompactMode();
        setFormTitle("Become Axe User");

        subTitleText.setText("Already have an account? ");

        subTitleLink.setText("Log in");
        subTitleLink.setHref(Endpoint.UI.LOGIN_PAGE);

        setFormSubTitle(subTitleText, subTitleLink);

        setupUserEmailSection();
        setupUserSection();
        setupUserRequirementsSection();
        setupPasswordSection();

        setFormFields(userEmailLayout, usernameRequirements, passwordLayout);

        setComponentsAfterFields(tosNote);
        setSubmitButtonText("Sign up");

        getSubmitButton().addClickListener(this::onRegister);
    }

    private void cleanInputs() {
        userEmailInput.clear();
        passwordInput.clear();
        usernameInput.clear();
    }

    private void onRegister(final ClickEvent<Button> event) {
        Notification.show("Not implemented yet");
    }


    private void setupUserEmailSection() {
        userEmailInput.setLabel("Username/Email");
        userEmailInput.setClearButtonVisible(true);
        userEmailInput.setTooltipText("Email stored encrypted. See requirements below.");
        userEmailInput.setClassName("input");
        userEmailInfoButton.setIconAfterText(true);
        userEmailInfoButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        userEmailInfoButton.setClassName("info-button");

        Tooltip userEmailTooltip = userEmailInput.getTooltip().withManual(true);
        userEmailInfoButton.addClickListener(event -> userEmailTooltip.setOpened(!userEmailTooltip.isOpened()));

        userEmailLayout.setAlignItems(Alignment.BASELINE);
        userEmailLayout.add(userEmailInput, userEmailInfoButton);
    }

    private void setupUserSection() {
        usernameInput.setLabel("Username");
        usernameInput.setClearButtonVisible(true);
        usernameInput.setTooltipText("You can use both as login");
        usernameInput.setClassName("input");

        usernameInfoButton.setIconAfterText(true);
        usernameInfoButton.setClassName("info-button");
        usernameInfoButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Tooltip usernameTooltip = usernameInput.getTooltip().withManual(true);
        usernameInfoButton.addClickListener(event -> usernameTooltip.setOpened(!usernameTooltip.isOpened()));

        usernameLayout.setAlignItems(Alignment.BASELINE);
        usernameLayout.add(usernameInput, usernameInfoButton);
    }

    private void setupUserRequirementsSection() {
        usernameRequirements.setSummaryText("Username requirements");
        Span span = new Span("Username should be");

        UnorderedList requirements = new UnorderedList();
        requirements.removeAll();
        Stream.of("The number of characters must be between 2 and 20.",
                        "Alphanumeric characters (a-zA-Z0-9), lowercase, or uppercase.",
                        "Also allowed of the dot (.), underscore (_), and hyphen (-).",
                        "The dot (.), underscore (_), or hyphen (-) must not be the first or last character.",
                        "The dot (.), underscore (_), or hyphen (-) does not appear consecutively, e.g., name..surname.")
                .forEach(requirement -> requirements.add(new ListItem(requirement)));

        usernameRequirements.addContent(span, requirements);
        usernameRequirements.setOpened(false);
    }

    private void setupPasswordSection() {
        passwordInput.setLabel("Password");
        passwordInput.setTooltipText("At least 3 chars. Use password generator - make it strong.");
        Tooltip passwordTooltip = passwordInput.getTooltip().withManual(true);
        passwordInfoButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        passwordInfoButton.addClickListener(event -> passwordTooltip.setOpened(!passwordTooltip.isOpened()));

        passwordInput.setLabel("Password");
        passwordInput.setClassName("input");
        passwordInput.setTooltipText("At least 3 chars. Use password generator - make it strong");

        passwordInfoButton.setIconAfterText(true);
        passwordInfoButton.setClassName("info-button");
        passwordInfoButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Tooltip usernameTooltip = passwordInput.getTooltip().withManual(true);
        passwordInfoButton.addClickListener(event -> usernameTooltip.setOpened(!usernameTooltip.isOpened()));

        passwordLayout.add(passwordInput, passwordInfoButton);
        passwordLayout.setAlignItems(Alignment.BASELINE);
    }

    private Span createLegalInfo() {
        Span tosStart = new Span("By signing up, you accept our ");
        Anchor linkToTerms = new Anchor();
        linkToTerms.setHref(Endpoint.UI.TOS_PAGE);
        linkToTerms.setText("Terms of Service");

        Span tosEnd = new Span(".");

        return new Span(tosStart, linkToTerms, tosEnd);
    }
}

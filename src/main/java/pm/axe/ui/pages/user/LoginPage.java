package pm.axe.ui.pages.user;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
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
import org.apache.commons.lang3.StringUtils;
import pm.axe.Endpoint;
import pm.axe.db.models.User;
import pm.axe.db.models.UserSettings;
import pm.axe.services.user.UserService;
import pm.axe.session.AxeSession;
import pm.axe.ui.MainView;
import pm.axe.ui.layouts.AxeFormLayout;
import pm.axe.users.LandingPage;
import pm.axe.utils.AppUtils;
import pm.axe.utils.AxeSessionUtils;

import java.util.Optional;


@SpringComponent
@UIScope
@RequiredArgsConstructor
@CssImport(value = "./css/login_page.css")
@Route(value = Endpoint.UI.LOGIN_PAGE, layout = MainView.class)
@PageTitle("Login Page - Axe.pm")
public class LoginPage extends AxeFormLayout implements BeforeEnterObserver {
    private final AppUtils appUtils;
    private final UserService userService;
    private final AxeSessionUtils axeSessionUtils;
    private final MainView mainView;

    private final Span subTitleText = new Span();
    private final Span spaceSpan = new Span();
    private final Anchor subTitleLink = new Anchor();

    private final TextField usernameInput = new TextField();
    private final PasswordField passwordInput = new PasswordField();
    private final Checkbox forgotMe = new Checkbox();
    private final Button forgotMeInfoButton = new Button(VaadinIcon.INFO_CIRCLE_O.create());

    private final Section forgotPasswordSection = new Section();
    private final Anchor forgotPasswordLink = new Anchor();

    private boolean pageAlreadyInitialized = false;
    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.isRefreshEvent()) return;
        if (pageAlreadyInitialized) {
           cleanInputs();
        } else {
            init();
            pageAlreadyInitialized = true;
        }
    }

    private void init() {
        setCompactMode();
        getForm().getStyle().set("width", "min(380px, 100%) !important"); //dirty fix for large screens

        setFormTitle("Sign in to Axe");
        subTitleText.setText("New to Axe?");
        spaceSpan.setText(" ");
        subTitleLink.setText("Register here");
        subTitleLink.setHref(Endpoint.UI.REGISTRATION_PAGE);

        setFormSubTitle(subTitleText, spaceSpan, subTitleLink);

        usernameInput.setLabel("Username/Email");
        usernameInput.setClearButtonVisible(true);
        usernameInput.setRequired(true);
        passwordInput.setLabel("Password");
        passwordInput.setRequired(true);

        forgotMe.setLabel("Log me out after");
        forgotMe.setTooltipText("If enabled, Axe will log you out once current session is over");
        forgotMeInfoButton.setIconAfterText(true);
        forgotMeInfoButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        forgotMeInfoButton.setClassName("info-button");

        Tooltip forgotMeTooltip = forgotMe.getTooltip().withManual(true);
        forgotMeInfoButton.addClickListener(e -> forgotMeTooltip.setOpened(!forgotMeTooltip.isOpened()));
        FlexLayout forgotMeLayout = new FlexLayout(forgotMe, forgotMeInfoButton);
        forgotMeLayout.setAlignItems(Alignment.BASELINE);

        setFormFields(usernameInput, passwordInput, forgotMeLayout);

        setSubmitButtonText("Jump in");
        getSubmitButton().addClickShortcut(Key.ENTER);
        getSubmitButton().addClickListener(this::onLogin);

        forgotPasswordLink.setHref(Endpoint.UI.FORGOT_PASSWORD_PAGE);
        forgotPasswordLink.setText("Forgot your password?");
        forgotPasswordLink.setClassName("forgot-password-link");
        forgotPasswordSection.add(forgotPasswordLink);
        forgotPasswordSection.setClassName("forgot-password-section");

        setComponentsAfterSubmitButton(forgotPasswordSection);
    }

    private void onLogin(final ClickEvent<Button> event) {
        //TODO do fields validation before sending anything
        //FIXME remove after real login progress implemented
        if (appUtils.isDevelopmentModeActivated()) {
            doEasyLogin();
        } else {
            Notification.show("Not implemented yet");
        }
    }

    private void doEasyLogin() {
        String username = StringUtils.isNotBlank(usernameInput.getValue()) ? usernameInput.getValue().trim() : "";
        if (StringUtils.isBlank(username)) {
            usernameInput.setInvalid(true);
            usernameInput.setErrorMessage("Please type username or email here");
        } else if (userService.isUserExists(username)) {
            AxeSession.getCurrent().ifPresent(axs -> {
                Optional<User> user = userService.getUserByUsername(username);
                user.ifPresent(axs::setUser);
                Optional<UserSettings> us = axeSessionUtils.getCurrentUserSettings();
                //dark mode
                if (us.isPresent() && us.get().isDarkMode()) {
                    axs.getSettings().setDarkMode(true);
                    mainView.applyTheme(true);
                }
                //landing page
                LandingPage landingPage; //page after login
                if (us.isPresent()) {
                    landingPage = us.get().getLandingPage();
                } else {
                    landingPage = LandingPage.HOME_PAGE;
                }
                usernameInput.getUI().ifPresent(ui -> ui.navigate(landingPage.getPath()));
            });
        } else {
            usernameInput.setInvalid(true);
            usernameInput.setErrorMessage("No such user found");
        }
    }

    private void cleanInputs() {
        usernameInput.clear();
        passwordInput.clear();
        forgotMe.clear();
    }
}

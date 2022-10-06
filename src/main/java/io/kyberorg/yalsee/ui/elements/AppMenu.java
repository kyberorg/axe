package io.kyberorg.yalsee.ui.elements;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import io.kyberorg.yalsee.utils.ErrorUtils;

public class AppMenu extends Composite<MenuBar> {

    /**
     * Creates menu, that should be shown to all visitors.
     *
     * @return application menu component with all items included.
     */
    public static AppMenu createNormalMenu() {
        return new AppMenu(false);
    }

    /**
     * Creates Menu for users logged in.
     *
     * @return application menu component with items for logged-in users.
     */
    public static AppMenu createUserMenu() {
        return new AppMenu(true);
    }

    public void moveUserButtonToFarRight() {
        getContent().getStyle().set("margin-left", "auto");
        getContent().getStyle().set("margin-right", "1rem");
    }

    private AppMenu(final boolean isUserMenu) {
        MenuBar menu = getContent();
        menu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        Button userButton = new Button();
        userButton.getStyle().set("border-radius", "100%");
        Component icon = isUserMenu ? VaadinIcon.SPECIALIST.create() : VaadinIcon.USER.create();
        userButton.setIcon(icon);

        MenuItem profile = menu.addItem(userButton);
        HorizontalLayout userMenuButtons = new HorizontalLayout();
        if (isUserMenu) {
            userMenuButtons.add(getLogoutButton());
        } else {
            userMenuButtons.add(getLoginButton(), getRegisterButton());
        }

        //buttons
        profile.getSubMenu().addItem(userMenuButtons);
        //line
        profile.getSubMenu().add(new Hr());

        if (isUserMenu) {
            profile.getSubMenu().addItem("Profile", this::openUserProfilePage);
        } else {
            //why register and terms
            profile.getSubMenu().addItem("Why register?", this::showWhyRegisterModal);
            profile.getSubMenu().addItem("Terms of service", this::openTermsOfServicePage);
        }
    }

    private Button getLogoutButton() {
        Button logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create());
        logoutButton.addClickListener(this::onLogoutButtonClicked);
        return logoutButton;
    }

    private Button getLoginButton() {
        Button loginButton = new Button("Log in", VaadinIcon.SIGN_IN.create());
        loginButton.addClickListener(this::onLoginButtonClicked);
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return loginButton;
    }

    private Button getRegisterButton() {
        Button registerButton = new Button("Register", VaadinIcon.CLIPBOARD_USER.create());
        registerButton.addClickListener(this::onRegisterButtonClicked);
        return registerButton;
    }

    private void onLogoutButtonClicked(final ClickEvent<Button> event) {
        showNotImplementedWarning();
    }

    private void onLoginButtonClicked(final ClickEvent<Button> event) {
        showNotImplementedWarning();
    }

    private void onRegisterButtonClicked(final ClickEvent<Button> event) {
        showNotImplementedWarning();
    }

    private void openUserProfilePage(final ClickEvent<MenuItem> event) {
        showNotImplementedWarning();
    }

    private void showWhyRegisterModal(final ClickEvent<MenuItem> event) {
        showNotImplementedWarning();
    }

    private void openTermsOfServicePage(final ClickEvent<MenuItem> event) {
        showNotImplementedWarning();
    }

    private void showNotImplementedWarning() {
        ErrorUtils.getErrorNotification("Not implemented yet").open();
    }
}

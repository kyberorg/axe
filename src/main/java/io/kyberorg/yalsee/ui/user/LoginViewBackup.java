package io.kyberorg.yalsee.ui.user;

import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.ui.HomeView;
import io.kyberorg.yalsee.ui.MainView;
import io.kyberorg.yalsee.ui.core.YalseeLayout;
import org.apache.commons.lang3.StringUtils;

/**
 * Login Page.
 *
 * @since 4.0
 */
@SpringComponent
@UIScope
@Route(value = Endpoint.UI.LOGIN_PAGE + "-backup", layout = MainView.class)
@PageTitle("Backup: Login Page")
public class LoginViewBackup extends YalseeLayout {

    public LoginViewBackup() {
        setId(IDs.PAGE_ID);

        LoginOverlay loginOverlay = new LoginOverlay();
        loginOverlay.setTitle("Yalsee");
        loginOverlay.setDescription("Built with ♥ by Kyberorg");

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setAdditionalInformation("To close the login form submit non-empty username and password");
        loginOverlay.setI18n(i18n);

        loginOverlay.addLoginListener(e -> {
            boolean isAuthenticated = authenticate(e);
            if (isAuthenticated) {
                getUI().ifPresent(ui -> ui.navigate(HomeView.class));
            } else {
                loginOverlay.setError(true);
            }
        });

        add(loginOverlay);
        loginOverlay.setOpened(true);
    }

    private boolean authenticate(AbstractLogin.LoginEvent e) {
        return !StringUtils.isAllBlank(e.getUsername(), e.getPassword());
    }

    public class IDs {
        public static final String PAGE_ID = "loginPage";
    }
}

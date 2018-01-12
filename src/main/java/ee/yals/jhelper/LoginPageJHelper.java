package ee.yals.jhelper;

import ee.yals.Endpoint;

import java.util.Objects;

/**
 * Non-static methods for loginPage.ftl
 *
 * @since 3.0
 */
@SuppressWarnings("unused") //used in LoginPage.FTL
public class LoginPageJHelper {
    private LoginPageJHelper() {
    }

    private static LoginPageJHelper self;

    public static synchronized LoginPageJHelper getInstance() {
        if (Objects.isNull(self)) {
            self = new LoginPageJHelper();
        }
        return self;
    }

    public String loginEndpoint() {
        return Endpoint.LOGIN_API;
    }
}

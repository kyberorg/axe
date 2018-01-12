package ee.yals.controllers;

import ee.yals.Endpoint;
import ee.yals.jhelper.LoginPageJHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Serves requests to login page (local auth)
 *
 * @since 3.0
 */
@Controller
public class LoginPage {

    /**
     * Login page
     *
     * @param params {@link java.util.Map}, which passes params ot FTL templates.
     *               In template they are reachable using 'params' name (set as annotation value)
     * @return Template (FTL) name
     */
    @RequestMapping(Endpoint.LOGIN_FORM)
    public String loginPage(@ModelAttribute("j") ModelMap params) {
        params.addAttribute("helper", LoginPageJHelper.getInstance());
        return "loginPage";
    }
}


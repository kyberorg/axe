package ee.yals.controllers.rest;

import ee.yals.Endpoint;
import ee.yals.core.TokenGenerator;
import ee.yals.json.ErrorJson;
import ee.yals.json.LoginRequestJson;
import ee.yals.json.LoginResponseJson;
import ee.yals.json.internal.Json;
import ee.yals.services.login.LoginService;
import ee.yals.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.util.Objects;
import java.util.Set;

/**
 * Deals with local logins
 *
 * @since 3.0
 */
@RestController
public class LoginRestController {

    @Autowired
    private LoginService loginService;

    @RequestMapping(method = RequestMethod.POST, value = Endpoint.LOGIN_API)
    public Json doLogin(@RequestBody String body, HttpServletResponse response) {
        LoginRequestJson loginInput;
        try {
            loginInput = AppUtils.GSON.fromJson(body, LoginRequestJson.class);
        } catch (Exception e) {
            response.setStatus(421);
            return ErrorJson.createWithMessage("Unable to parse incoming JSON");
        }

        Set<ConstraintViolation> errors = AppUtils.ValidationHelper.validateJsonFields(loginInput);
        if (errors != AppUtils.ValidationHelper.EMPTY_SET) {
            response.setStatus(400);
            return ErrorJson.createFromSetOfErrors(errors);
        }

        if (Objects.isNull(loginInput)) {
            response.setStatus(500);
            return ErrorJson.createWithMessage("Application error: not all components are available");
        }

        if (loginService.userNotValid(loginInput.getUsername())) {
            response.setStatus(401);
            return ErrorJson.createWithMessage("No such user or password is wrong");
        }

        if (loginService.passwordNotValid(loginInput.getPlainPass(), loginInput.getUsername())) {
            response.setStatus(401);
            return ErrorJson.createWithMessage("No such user or password is wrong");
        }

        TokenGenerator.generateNew();
        String token;
        do {
            token = TokenGenerator.generateNew();
        } while (loginService.isTokenAlreadyExists(token));

        response.setStatus(200);
        return LoginResponseJson.createWithToken(token);
    }

}

package ee.yals.controllers.rest;

import ee.yals.Endpoint;
import ee.yals.core.IdentGenerator;
import ee.yals.json.ErrorJson;
import ee.yals.json.StoreRequestJson;
import ee.yals.json.StoreResponseJson;
import ee.yals.json.internal.Json;
import ee.yals.models.User;
import ee.yals.result.GetResult;
import ee.yals.result.StoreResult;
import ee.yals.services.LinkService;
import ee.yals.services.token.TokenService;
import ee.yals.utils.AppUtils;
import ee.yals.utils.UrlExtraValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

import static ee.yals.services.token.TokenService.NO_TOKEN;

/**
 * Stores long link to storage
 *
 * @since 1.0
 */
@RestController
public class StoreRestController {

    @Autowired
    @Qualifier("dbStorage")
    private LinkService linkService;

    @Autowired
    private TokenService tokenService;

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT},
            value = Endpoint.STORE_API)
    public Json store(@RequestBody String body, HttpServletResponse response) {
        StoreRequestJson storeInput;
        try {
            storeInput = AppUtils.GSON.fromJson(body, StoreRequestJson.class);
        } catch (Exception e) {
            response.setStatus(421);
            return ErrorJson.createWithMessage("Unable to parse json");
        }

        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<StoreRequestJson>> errors = validator.validate(storeInput);
        Set<ConstraintViolation> errors1 = new HashSet<>();
        if (!errors.isEmpty()) {
            errors1.addAll(errors);
            response.setStatus(421);
            return ErrorJson.createFromSetOfErrors(errors1);
        }

        String messageFromExtraValidator = UrlExtraValidator.isUrlValid(storeInput.getLink());
        if(! messageFromExtraValidator.equals(UrlExtraValidator.VALID)){
            response.setStatus(421);
            return ErrorJson.createWithMessage(messageFromExtraValidator);
        }

        String usersIdent = ""; //TODO replace by data from JSON
        boolean usingUsersIdent = isUsersIdentValid(usersIdent);
        String ident;
        if (usingUsersIdent) {
            if (isIdentAlreadyExists(usersIdent)) {
                response.setStatus(407); //conflict
                return ErrorJson.createWithMessage("We already have link stored with given ident:" + usersIdent + " Try another one");
            } else {
                ident = usersIdent;
            }
        } else {
            do {
                ident = IdentGenerator.generateNewIdent();
            } while (isIdentAlreadyExists(ident));
        }

        //TODO get token from JSON
        User linkOwner = tokenService.giveUserFromToken(NO_TOKEN);

        StoreResult result = linkService.storeNew(ident, storeInput.getLink(), linkOwner);
        if (result instanceof StoreResult.Success) {
            response.setStatus(201);
            return StoreResponseJson.create().withIdent(ident);
        } else {
            response.setStatus(500);
            return ErrorJson.createWithMessage("Failed to save your link. Internal server error.");
        }
    }

    private boolean isUsersIdentValid(String usersIdent) {
        return usersIdent.matches(IdentGenerator.VALID_IDENT_PATTERN);
    }

    private boolean isIdentAlreadyExists(String ident) {
        GetResult searchResult = linkService.getLink(ident);
        return (searchResult instanceof GetResult.Success);
    }

}

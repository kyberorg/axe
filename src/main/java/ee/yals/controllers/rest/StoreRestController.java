package ee.yals.controllers.rest;

import ee.yals.Endpoint;
import ee.yals.core.IdentGenerator;
import ee.yals.json.ErrorJson;
import ee.yals.json.StoreRequestJson;
import ee.yals.json.StoreResponseJson;
import ee.yals.json.internal.Json;
import ee.yals.result.GetResult;
import ee.yals.result.StoreResult;
import ee.yals.services.LinkService;
import ee.yals.utils.AppUtils;
import ee.yals.utils.UrlExtraValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

/**
 * Stores long link to storage
 *
 * @since 1.0
 */
@RestController
@Slf4j
public class StoreRestController {
    private static final String TAG = "[API Store]";

    private final LinkService linkService;
    private final AppUtils appUtils;

    public StoreRestController(@Qualifier("dbStorage") LinkService linkService, AppUtils appUtils) {
        this.linkService = linkService;
        this.appUtils = appUtils;
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT},
            value = Endpoint.STORE_API)
    public Json store(@RequestBody String body, HttpServletResponse response) {
        log.info(String.format("%s got request: %s", TAG, body));

        StoreRequestJson storeInput;
        try {
            storeInput = AppUtils.GSON.fromJson(body, StoreRequestJson.class);
        } catch (Exception e) {
            response.setStatus(421);
            log.info(String.format("%s unparseable JSON", TAG));
            return ErrorJson.createWithMessage("Unable to parse json");
        }

        String linkToStore = storeInput.getLink();
        if (StringUtils.isNotBlank(linkToStore)) {
            //normalize URL if needed
            try {
                String cleanLink = appUtils.makeFullUri(linkToStore).toString();
                log.trace("{} Link {} became {} after adding schema", TAG, linkToStore, cleanLink);
                String link = appUtils.decodeUrl(cleanLink);
                log.trace("{} Link {} became {} after decoding", TAG, cleanLink, link);
                storeInput.withLink(link);
            } catch (RuntimeException e) {
                //Malformed URL: will be handled by validators later on
            }
        }

        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<StoreRequestJson>> errors = validator.validate(storeInput);
        Set<ConstraintViolation> errors1 = new HashSet<>();
        if (!errors.isEmpty()) {
            log.info(String.format("%s Value Violations found: %s", TAG, errors));
            errors1.addAll(errors);
            response.setStatus(421);
            return ErrorJson.createFromSetOfErrors(errors1);
        }

        String messageFromExtraValidator = UrlExtraValidator.isUrlValid(storeInput.getLink());
        if (!messageFromExtraValidator.equals(UrlExtraValidator.VALID)) {
            log.info(String.format("%s not valid URL: %s", TAG, messageFromExtraValidator));
            response.setStatus(421);
            return ErrorJson.createWithMessage(messageFromExtraValidator);
        }

        String usersIdent = ""; //TODO replace by data from JSON
        boolean usingUsersIdent = isUsersIdentValid(usersIdent);
        String ident;
        if (usingUsersIdent) {
            if (isIdentAlreadyExists(usersIdent)) {
                log.info(String.format("%s User Ident '%s' already exists", TAG, usersIdent));
                log.debug(String.format("%s Conflicting ident: %s", TAG, usersIdent));
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

        StoreResult result = linkService.storeNew(ident, storeInput.getLink());
        if (result instanceof StoreResult.Success) {
            log.info(String.format("%s Saved. {\"ident\": %s, \"link\": %s}", TAG, ident, storeInput.getLink()));
            response.setStatus(201);
            return StoreResponseJson.create().withIdent(ident);
        } else if (result instanceof StoreResult.Fail) {
            log.error(String.format("%s Failed to save link: %s", TAG, storeInput.getLink()));
            response.setStatus(500);
            return ErrorJson.createWithMessage("Failed to save your link. Internal server error.");
        } else {
            log.error(String.format("%s Failed to save link: got unknown result object: %s", TAG, result));
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

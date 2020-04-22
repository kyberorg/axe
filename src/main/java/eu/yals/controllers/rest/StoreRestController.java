package eu.yals.controllers.rest;

import eu.yals.Endpoint;
import eu.yals.core.IdentGenerator;
import eu.yals.json.ErrorJson;
import eu.yals.json.StoreRequestJson;
import eu.yals.json.StoreResponseJson;
import eu.yals.json.internal.Json;
import eu.yals.result.GetResult;
import eu.yals.result.StoreResult;
import eu.yals.services.LinkService;
import eu.yals.utils.AppUtils;
import eu.yals.utils.UrlExtraValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import static eu.yals.constants.HttpCode.*;

/**
 * Stores long link to storage.
 *
 * @since 1.0
 */
@Slf4j
@RestController
public class StoreRestController {
    private static final String TAG = "[" + StoreRestController.class.getSimpleName() + "]";

    private final LinkService linkService;

    /**
     * Constructor for Spring autowiring.
     *
     * @param linksService service which stores links to DB
     */
    public StoreRestController(final LinkService linksService) {
        this.linkService = linksService;
    }

    /**
     * API Endpoint to store link into DB.
     *
     * @param body     string with body of HTTP request
     * @param response response
     * @return json with reply
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT},
            value = Endpoint.Api.STORE_API)
    public Json store(final @RequestBody String body, final HttpServletResponse response) {
        log.info("{} got request: {}", TAG, body);

        StoreRequestJson storeInput;
        try {
            storeInput = AppUtils.GSON.fromJson(body, StoreRequestJson.class);
        } catch (Exception e) {
            response.setStatus(STATUS_421);
            log.info("{} unparseable JSON", TAG);
            return ErrorJson.createWithMessage("Unable to parse json");
        }

        String linkToStore = storeInput.getLink();
        if (StringUtils.isNotBlank(linkToStore)) {
            //normalize URL if needed
            try {
                String fullUrl = AppUtils.makeFullUri(linkToStore).toString();
                log.trace("{} Link {} became {} after adding schema", TAG, linkToStore, fullUrl);
                storeInput.withLink(fullUrl);
            } catch (RuntimeException e) {
                //Malformed URL: will be handled by validators later on
            }
        }

        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<StoreRequestJson>> errors = validator.validate(storeInput);
        if (!errors.isEmpty()) {
            log.info("{} Value Violations found: {}", TAG, errors);
            Set<ConstraintViolation> errorSet = new HashSet<>(errors);
            response.setStatus(STATUS_421);
            return ErrorJson.createFromSetOfErrors(errorSet);
        }

        String messageFromExtraValidator = UrlExtraValidator.isUrlValid(storeInput.getLink());
        if (!messageFromExtraValidator.equals(UrlExtraValidator.VALID)) {
            log.info("{} not valid URL: {}", TAG, messageFromExtraValidator);
            response.setStatus(STATUS_421);
            return ErrorJson.createWithMessage(messageFromExtraValidator);
        }

        String usersIdent = ""; //TODO replace by data from JSON
        boolean usingUsersIdent = isUsersIdentValid(usersIdent);
        String ident;
        if (usingUsersIdent) {
            if (isIdentAlreadyExists(usersIdent)) {
                log.info("{} User Ident '{}' already exists", TAG, usersIdent);
                log.debug("{} Conflicting ident: {}", TAG, usersIdent);
                response.setStatus(STATUS_409); //conflict
                return ErrorJson.createWithMessage("We already have link stored with given ident:" + usersIdent
                        + " Try another one");
            } else {
                ident = usersIdent;
            }
        } else {
            do {
                ident = IdentGenerator.generateNewIdent();
            } while (isIdentAlreadyExists(ident));
        }

        //decoding URL before saving to DB
        try {
            String currentLink = storeInput.getLink();
            String decodedLink = AppUtils.decodeUrl(currentLink);
            log.trace("{} Link {} became {} after decoding", TAG, currentLink, decodedLink);
            storeInput.withLink(decodedLink);
        } catch (RuntimeException e) {
            String message = "Problem with URL decoding";
            log.error(message, e);
            response.setStatus(STATUS_500);
            return ErrorJson.createWithMessage(message);
        }

        StoreResult result = linkService.storeNew(ident, storeInput.getLink());
        if (result instanceof StoreResult.Success) {
            log.info("{} Saved. {\"ident\": {}, \"link\": {}}", TAG, ident, storeInput.getLink());
            response.setStatus(STATUS_201);
            return StoreResponseJson.create().withIdent(ident);
        } else if (result instanceof StoreResult.Fail) {
            log.error("{} Failed to save link: {}", TAG, storeInput.getLink());
            response.setStatus(STATUS_500);
            return ErrorJson.createWithMessage("Failed to save your link. Internal server error.");
        } else if (result instanceof StoreResult.DatabaseDown) {
            response.setStatus(STATUS_503);
            log.error("{} Database is DOWN", TAG, ((StoreResult.DatabaseDown) result).getException());
            return ErrorJson.createWithMessage("The server is currently unable to handle the request");
        } else {
            log.error("{} Failed to save link: got unknown result object: {}", TAG, result);
            response.setStatus(STATUS_500);
            return ErrorJson.createWithMessage("Failed to save your link. Internal server error.");
        }
    }

    private boolean isUsersIdentValid(final String usersIdent) {
        return usersIdent.matches(IdentGenerator.VALID_IDENT_PATTERN);
    }

    private boolean isIdentAlreadyExists(final String ident) {
        GetResult searchResult = linkService.getLink(ident);
        return (searchResult instanceof GetResult.Success);
    }
}

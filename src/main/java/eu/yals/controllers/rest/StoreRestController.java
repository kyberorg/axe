package eu.yals.controllers.rest;

import eu.yals.Endpoint;
import eu.yals.core.IdentGenerator;
import eu.yals.json.StoreRequestJson;
import eu.yals.json.StoreResponseJson;
import eu.yals.json.YalsErrorJson;
import eu.yals.json.YalsJson;
import eu.yals.result.GetResult;
import eu.yals.result.StoreResult;
import eu.yals.services.LinkService;
import eu.yals.utils.AppUtils;
import eu.yals.utils.Result;
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
@SuppressWarnings("rawtypes")
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
    public YalsJson store(final @RequestBody String body, final HttpServletResponse response) {
        log.info("{} got request: {}", TAG, body);

        Result parseResult = parseJson(body);

        if (resultHasYalsErrorJson(parseResult)) {
            return yalsErrorJson(parseResult, response);
        }

        StoreRequestJson storeInput = parseResult.read(StoreRequestJson.class);
        String linkToStore = storeInput.getLink();

        storeInput.setLink(normalizeUrl(linkToStore));

        Result validateResult = validateInput(storeInput);
        if (resultHasYalsErrorJson(validateResult)) {
            return yalsErrorJson(validateResult, response);
        }

        String usersIdent = ""; //TODO replace by data from JSON
        boolean usingUsersIdent = isUsersIdentValid(usersIdent);
        String ident;
        if (usingUsersIdent) {
            if (isIdentAlreadyExists(usersIdent)) {
                response.setStatus(STATUS_409); //conflict
                return conflict(usersIdent);
            } else {
                ident = usersIdent;
            }
        } else {
            do {
                ident = IdentGenerator.generateNewIdent();
            } while (isIdentAlreadyExists(ident));
        }

        //decoding URL before saving to DB
        Result decodeUrlResult = decodeUrl(storeInput.getLink(), response);
        if (resultHasYalsErrorJson(decodeUrlResult)) {
            return yalsErrorJson(decodeUrlResult, response);
        }
        String decodedUrl = decodeUrlResult.read(String.class);
        return storeLink(ident, decodedUrl, response);
    }

    private Result decodeUrl(final String currentLink, final HttpServletResponse response) {
        try {
            String decodedLink = AppUtils.decodeUrl(currentLink);
            log.trace("{} Link {} became {} after decoding", TAG, currentLink, decodedLink);
            return Result.get().write(decodedLink);
        } catch (RuntimeException e) {
            String message = "Problem with URL decoding";
            log.error("{} {}", TAG, message);
            response.setStatus(STATUS_500);

            YalsErrorJson errorJson = YalsErrorJson.builder()
                    .message(message).techMessage(e.getMessage()).throwable(e)
                    .status(STATUS_500)
                    .build();
            return Result.get().write(errorJson);
        }
    }

    private Result parseJson(final String body) {
        try {
            StoreRequestJson storeInput = AppUtils.GSON.fromJson(body, StoreRequestJson.class);
            return Result.get().write(storeInput);
        } catch (Exception e) {
            log.info("{} unparseable JSON", TAG);
            YalsErrorJson errorJson = YalsErrorJson.builder()
                    .status(STATUS_421)
                    .message("Unable to parse json")
                    .techMessage("Malformed JSON received. Got body: " + body)
                    .build();
            return Result.get().write(errorJson);
        }
    }

    private String normalizeUrl(final String linkToStore) {
        if (StringUtils.isNotBlank(linkToStore)) {
            //normalize URL if needed
            try {
                String fullUrl = AppUtils.makeFullUri(linkToStore).toString();
                log.trace("{} Link {} became {} after adding schema", TAG, linkToStore, fullUrl);
                return fullUrl;
            } catch (RuntimeException e) {
                //to be handled by validators
                return linkToStore;
            }
        } else {
            return linkToStore;
        }
    }

    private Result validateInput(final StoreRequestJson storeInput) {
        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<StoreRequestJson>> errors = validator.validate(storeInput);
        if (!errors.isEmpty()) {
            log.info("{} Value Violations found: {}", TAG, errors);
            Set<ConstraintViolation> errorSet = new HashSet<>(errors);
            YalsErrorJson errorJson = YalsErrorJson.createFromSetOfErrors(errorSet).andStatus(STATUS_421);
            return Result.get().write(errorJson);
        }

        String messageFromExtraValidator = UrlExtraValidator.isUrlValid(storeInput.getLink());
        if (!messageFromExtraValidator.equals(UrlExtraValidator.VALID)) {
            log.info("{} not valid URL: {}", TAG, messageFromExtraValidator);
            YalsErrorJson errorJson = YalsErrorJson.createWithMessage(messageFromExtraValidator).andStatus(STATUS_421);
            return Result.get().write(errorJson);
        }
        return Result.get().write("Validation passed");
    }

    private YalsJson storeLink(final String ident, final String decodedUrl, final HttpServletResponse response) {
        StoreResult result = linkService.storeNew(ident, decodedUrl);
        if (result instanceof StoreResult.Success) {
            log.info("{} Saved. {\"ident\": {}, \"link\": {}}", TAG, ident, decodedUrl);
            response.setStatus(STATUS_201);
            return StoreResponseJson.create().withIdent(ident);
        } else if (result instanceof StoreResult.Fail) {
            log.error("{} Failed to save link: {}", TAG, decodedUrl);
            response.setStatus(STATUS_500);
            return YalsErrorJson.createWithMessage("Failed to save your link. Internal server error.");
        } else if (result instanceof StoreResult.DatabaseDown) {
            response.setStatus(STATUS_503);
            log.error("{} Database is DOWN", TAG, ((StoreResult.DatabaseDown) result).getException());
            return YalsErrorJson.createWithMessage("The server is currently unable to handle the request")
                    .andStatus(STATUS_503);
        } else {
            log.error("{} Failed to save link: got unknown result object: {}", TAG, result);
            response.setStatus(STATUS_500);
            return YalsErrorJson.createWithMessage("Failed to save your link. Internal server error.");
        }
    }

    private boolean isUsersIdentValid(final String usersIdent) {
        return usersIdent.matches(IdentGenerator.VALID_IDENT_PATTERN);
    }

    private boolean isIdentAlreadyExists(final String ident) {
        GetResult searchResult = linkService.getLink(ident);
        return (searchResult instanceof GetResult.Success);
    }

    private boolean resultHasYalsErrorJson(final Result result) {
        if (result == null) return false;
        return result.readValueType(Result.DEFAULT_KEY) == YalsErrorJson.class;
    }

    private YalsErrorJson yalsErrorJson(final Result result, final HttpServletResponse response) {
        YalsErrorJson errorJson = result.read(YalsErrorJson.class);
        response.setStatus(errorJson.getStatus());
        return errorJson;
    }

    private YalsErrorJson conflict(final String usersIdent) {
        log.info("{} User Ident '{}' already exists", TAG, usersIdent);
        log.debug("{} Conflicting ident: {}", TAG, usersIdent);
        return YalsErrorJson.createWithMessage("We already have link stored with given ident:" + usersIdent
                + " Try another one").andStatus(STATUS_409);
    }
}

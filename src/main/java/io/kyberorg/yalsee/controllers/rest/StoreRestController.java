package io.kyberorg.yalsee.controllers.rest;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.core.BanHammer;
import io.kyberorg.yalsee.core.IdentGenerator;
import io.kyberorg.yalsee.json.StoreRequestJson;
import io.kyberorg.yalsee.json.StoreResponseJson;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.json.YalseeJson;
import io.kyberorg.yalsee.result.GetResult;
import io.kyberorg.yalsee.result.StoreResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.utils.AppUtils;
import io.kyberorg.yalsee.utils.Result;
import io.kyberorg.yalsee.utils.UrlExtraValidator;
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
    public YalseeJson store(final @RequestBody String body, final HttpServletResponse response) {
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

        if (BanHammer.shouldBeBanned(linkToStore)) {
            response.setStatus(HttpCode.STATUS_403);
            return banned(linkToStore);
        }

        String usersIdent = ""; //TODO replace by data from JSON
        boolean usingUsersIdent = isUsersIdentValid(usersIdent);
        String ident;
        if (usingUsersIdent) {
            if (isIdentAlreadyExists(usersIdent)) {
                response.setStatus(HttpCode.STATUS_409); //conflict
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
            response.setStatus(HttpCode.STATUS_500);

            YalseeErrorJson errorJson = YalseeErrorJson.builder()
                    .message(message).techMessage(e.getMessage()).throwable(e)
                    .status(HttpCode.STATUS_500)
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
            YalseeErrorJson errorJson = YalseeErrorJson.builder()
                    .status(HttpCode.STATUS_421)
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
                String convertedUrl = AppUtils.covertUnicodeToAscii(fullUrl);
                log.trace("{} Link {} converted to {}", TAG, fullUrl, convertedUrl);
                return convertedUrl;
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
            YalseeErrorJson errorJson = YalseeErrorJson.createFromSetOfErrors(errorSet).andStatus(HttpCode.STATUS_421);
            return Result.get().write(errorJson);
        }

        String messageFromExtraValidator = UrlExtraValidator.isUrlValid(storeInput.getLink());
        Result result;
        switch (messageFromExtraValidator) {
            case UrlExtraValidator.VALID:
                result = Result.get().write("Validation passed");
                break;
            case UrlExtraValidator.LOCAL_URL_NOT_ALLOWED:
                log.info("{} {} is not allowed", TAG, storeInput.getLink());
                YalseeErrorJson errorJson = YalseeErrorJson.createWithMessage(messageFromExtraValidator)
                        .andStatus(HttpCode.STATUS_403);
                result = Result.get().write(errorJson);
                break;
            case UrlExtraValidator.URL_NOT_VALID:
            default:
                log.info("{} not valid URL: {}", TAG, messageFromExtraValidator);
                YalseeErrorJson errorJson1 = YalseeErrorJson.createWithMessage(messageFromExtraValidator)
                        .andStatus(HttpCode.STATUS_421);
                result = Result.get().write(errorJson1);
                break;
        }
        return result;
    }

    private YalseeJson storeLink(final String ident, final String decodedUrl, final HttpServletResponse response) {
        StoreResult result = linkService.storeNew(ident, decodedUrl);
        if (result instanceof StoreResult.Success) {
            log.info("{} Saved. {\"ident\": {}, \"link\": {}}", TAG, ident, decodedUrl);
            response.setStatus(HttpCode.STATUS_201);
            return StoreResponseJson.create().withIdent(ident);
        } else if (result instanceof StoreResult.Fail) {
            log.error("{} Failed to save link: {}", TAG, decodedUrl);
            response.setStatus(HttpCode.STATUS_500);
            return YalseeErrorJson.createWithMessage("Failed to save your link. Internal server error.");
        } else if (result instanceof StoreResult.DatabaseDown) {
            response.setStatus(HttpCode.STATUS_503);
            log.error("{} Database is DOWN", TAG, ((StoreResult.DatabaseDown) result).getException());
            return YalseeErrorJson.createWithMessage("The server is currently unable to handle the request")
                    .andStatus(HttpCode.STATUS_503);
        } else {
            log.error("{} Failed to save link: got unknown result object: {}", TAG, result);
            response.setStatus(HttpCode.STATUS_500);
            return YalseeErrorJson.createWithMessage("Failed to save your link. Internal server error.");
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
        return result.readValueType(Result.DEFAULT_KEY) == YalseeErrorJson.class;
    }

    private YalseeErrorJson yalsErrorJson(final Result result, final HttpServletResponse response) {
        YalseeErrorJson errorJson = result.read(YalseeErrorJson.class);
        response.setStatus(errorJson.getStatus());
        return errorJson;
    }

    private YalseeErrorJson conflict(final String usersIdent) {
        log.info("{} User Ident '{}' already exists", TAG, usersIdent);
        log.debug("{} Conflicting ident: {}", TAG, usersIdent);
        return YalseeErrorJson.createWithMessage("We already have link stored with given ident:" + usersIdent
                + " Try another one").andStatus(HttpCode.STATUS_409);
    }

    private YalseeErrorJson banned(final String bannedUrl) {
        log.info("{} URL '{}' is banned", TAG, bannedUrl);
        return YalseeErrorJson.createWithMessage("URL is banned. Try another one")
                .andStatus(HttpCode.STATUS_403);
    }
}

package pm.axe.services;

import pm.axe.configuration.EndpointsListener;
import pm.axe.core.BanHammer;
import pm.axe.core.IdentGenerator;
import pm.axe.db.dao.LinkDao;
import pm.axe.events.link.LinkDeletedEvent;
import pm.axe.events.link.LinkSavedEvent;
import pm.axe.events.link.LinkUpdatedEvent;
import pm.axe.exception.URLDecodeException;
import pm.axe.internal.LinkServiceInput;
import pm.axe.db.models.Link;
import pm.axe.db.models.User;
import pm.axe.result.OperationResult;
import pm.axe.users.TokenType;
import pm.axe.utils.AppUtils;
import pm.axe.utils.UrlExtraValidator;
import pm.axe.utils.UrlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.Optional;

/**
 * Service, which interacts with database to store/retrieve links.
 *
 * @since 2.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LinkService {
    private static final String TAG = "[" + LinkService.class.getSimpleName() + "]";
    private final LinkDao repo;
    private final LinkInfoService linkInfoService;
    private final EndpointsListener endpointsListener;
    private final AppUtils appUtils;

    public static final String OP_MALFORMED_IDENT = "Ident is not valid";
    public static final String OP_MALFORMED_URL = UrlExtraValidator.URL_NOT_VALID;
    public static final String OP_URL_BANNED = "URL is banned";
    public static final String OP_LOCAL_URL_BANNED = UrlExtraValidator.LOCAL_URL_NOT_ALLOWED;

    /**
     * Delete link with given ident from DB.
     *
     * @param ident string with ident searching link
     * @return {@link OperationResult} object with exec status and error message if applicable
     */
    public OperationResult deleteLinkWithIdent(final String ident) {
        Optional<Link> link;
        try {
            link = repo.findSingleByIdent(ident);
            if (link.isPresent()) {
                repo.delete(link.get());
                linkInfoService.deleteLinkInfo(ident);
                EventBus.getDefault().post(LinkDeletedEvent.createWith(link.get()));
                return OperationResult.success();
            } else {
                return OperationResult.elementNotFound();
            }
        } catch (DataAccessResourceFailureException dbEx) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Provides long link by its ident.
     *
     * @param ident string with ident for searching link
     * @return {@link OperationResult} object with exec status, payload (string containing long link) or error message.
     */
    public OperationResult getLinkWithIdent(final String ident) {
        Optional<Link> link;
        try {
            link = repo.findSingleByIdent(ident);
            if (link.isPresent()) {
                return OperationResult.success().addPayload(link.get().getLink());
            } else {
                return OperationResult.elementNotFound();
            }
        } catch (DataAccessResourceFailureException dbEx) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Provides {@link Link} by its ident.
     *
     * @param ident string with ident for searching link
     * @return {@link OperationResult} object with exec status, payload {@link Link} or error message.
     */
    public OperationResult getLinkByIdent(final String ident) {
        Optional<Link> link;
        try {
            link = repo.findSingleByIdent(ident);
            if (link.isPresent()) {
                return OperationResult.success().addPayload(link.get());
            } else {
                return OperationResult.elementNotFound();
            }
        } catch (DataAccessResourceFailureException dbEx) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Provides info if link with given ident stored or not.
     *
     * @param ident string with ident for searching against.
     * @return {@link OperationResult#OK} when record exists,
     * {@link OperationResult#ELEMENT_NOT_FOUND} when nothing stored
     * {@link OperationResult#SYSTEM_DOWN} when database unreachable
     * {@link OperationResult#GENERAL_FAIL} when something unexpected happened
     */
    public OperationResult isLinkWithIdentExist(final String ident) {
        long links;
        try {
            links = repo.countByIdent(ident);
            if (links > 0) {
                return OperationResult.success();
            } else {
                return OperationResult.elementNotFound();
            }
        } catch (DataAccessResourceFailureException dbEx) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Stores new link with user-defined ident or autogenerated (if ident is null).
     *
     * @param input {@link LinkServiceInput} object with params inside.
     * @return {@link OperationResult} object with:
     * <p>
     * {@link OperationResult#OK} with stored {@link Link} object in payload.
     * {@link OperationResult#MALFORMED_INPUT} with {@link #OP_MALFORMED_URL} message, if long link is malformed.
     * {@link OperationResult#BANNED} with {@link #OP_LOCAL_URL_BANNED} message, if long link is single-layered URL.
     * {@link OperationResult#BANNED} with {@link #OP_URL_BANNED} message, if long link is banned URL.
     * {@link OperationResult#MALFORMED_INPUT} with {@link #OP_MALFORMED_IDENT} message, if ident is malformed.
     * {@link OperationResult#CONFLICT}, if ident is already exists.
     * {@link OperationResult#GENERAL_FAIL}, if something unexpected happened at server-side.
     * {@link OperationResult#SYSTEM_DOWN}, if system is partially or completely not available.
     */
    public OperationResult createLink(final LinkServiceInput input) {
        //Link validation
        OperationResult urlValidationResult = validateUrl(input.getLink());
        String urlToStore;
        if (urlValidationResult.ok()) {
            urlToStore = urlValidationResult.getStringPayload();
        } else {
            return urlValidationResult;
        }

        //Check if URL is banned
        if (BanHammer.shouldBeBanned(urlToStore)) {
            log.info("{} URL '{}' is banned", TAG, urlToStore);
            return OperationResult.banned().withMessage(OP_URL_BANNED);
        }

        //Ident validation or generation
        String ident;
        if (input.getCustomIdent() != null) {
            OperationResult identValidationResult = validateIdent(input.getCustomIdent());
            if (identValidationResult.ok()) {
                ident = identValidationResult.getStringPayload();
            } else {
                return identValidationResult;
            }
        } else {
            ident = generateIdent();
        }

        //Action
        try {
            //decoding URL before saving to DB
            String decodedLink = UrlUtils.decodeUrl(urlToStore);
            log.trace("{} Link {} became {} after decoding", TAG, urlToStore, decodedLink);

            //action
            Link linkObject = Link.create(ident, decodedLink);
            Link savedLink = repo.save(linkObject);

            String sessionId = input.getSessionID();
            String description = input.getDescription();
            User owner = input.getLinkOwner();

            linkInfoService.createLinkInfo(ident, sessionId, description, owner);

            log.info("{} Saved. {\"ident\": {}, \"link\": {}}", TAG, ident, decodedLink);
            EventBus.getDefault().post(LinkSavedEvent.createWith(savedLink));
            return OperationResult.success().addPayload(savedLink);
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (URLDecodeException decodeException) {
            log.error("{} {}", TAG, decodeException.getMessage());
            return OperationResult.generalFail().withMessage("Failed to decode URL");
        } catch (Exception e) {
            log.error("{} Exception on storing new {}", TAG, Link.class.getSimpleName());
            log.debug("", e);
            return OperationResult.generalFail();
        }
    }

    /**
     * Updates {@link Link} record.
     *
     * @param newLink updated {@link Link} object.
     * @return {@link OperationResult} object with:
     * <p>
     * {@link OperationResult#OK} with stored {@link Link} object in payload.
     * {@link OperationResult#MALFORMED_INPUT} with {@link #OP_MALFORMED_URL} message, if long link is malformed.
     * {@link OperationResult#BANNED} with {@link #OP_LOCAL_URL_BANNED} message, if long link is single-layered URL.
     * {@link OperationResult#BANNED} with {@link #OP_URL_BANNED} message, if long link is banned URL.
     * {@link OperationResult#MALFORMED_INPUT} with {@link #OP_MALFORMED_IDENT} message, if ident is malformed.
     * {@link OperationResult#CONFLICT}, if ident is already exists.
     * {@link OperationResult#GENERAL_FAIL}, if something unexpected happened at server-side.
     * {@link OperationResult#SYSTEM_DOWN}, if system is partially or completely not available.
     */
    public OperationResult updateLink(final Link newLink) {
        //Link validation
        OperationResult urlValidationResult = validateUrl(newLink.getLink());
        if (urlValidationResult.notOk()) {
            return urlValidationResult;
        }
        //Check if URL is banned
        if (BanHammer.shouldBeBanned(newLink.getLink())) {
            log.info("{} URL '{}' is banned", TAG, newLink.getLink());
            return OperationResult.banned().withMessage(OP_URL_BANNED);
        }

        //ident validation and check on conflict with already existing ident.
        OperationResult identValidationResult = validateIdent(newLink.getIdent());
        if (identValidationResult.notOk()) {
            return identValidationResult;
        }

        //Action
        try {
            //decoding URL before saving to DB
            String decodedLink = UrlUtils.decodeUrl(newLink.getLink());
            log.trace("{} Link {} became {} after decoding", TAG, newLink.getLink(), decodedLink);
            newLink.setLink(decodedLink);

            //action
            Link updatedLink = repo.save(newLink);
            log.info("{} Updated. {\"ident\": {}, \"link\": {}}", TAG, updatedLink.getIdent(), decodedLink);
            EventBus.getDefault().post(LinkUpdatedEvent.createWith(updatedLink));
            return OperationResult.success().addPayload(updatedLink);
        } catch (CannotCreateTransactionException e) {
            return OperationResult.databaseDown();
        } catch (URLDecodeException decodeException) {
            log.error("{} {}", TAG, decodeException.getMessage());
            return OperationResult.generalFail().withMessage("Failed to decode URL");
        } catch (Exception e) {
            log.error("{} Exception on storing new {}", TAG, Link.class.getSimpleName());
            log.debug("", e);
            return OperationResult.generalFail();
        }
    }

    /**
     * Makes long link short, based on {@link TokenType}.
     *
     * @param longLink  non-empty string with long link.
     * @param tokenType token type to determine ident prefix.
     * @return {@link OperationResult#success()} with short in {@link OperationResult#payload}
     * or {@link OperationResult} from {@link #createLink(LinkServiceInput)} method.
     */
    public OperationResult shortifyLinkForTokens(final String longLink, final TokenType tokenType) {
        String ident;
        do {
            ident = IdentGenerator.generateTokenIdent(tokenType);
        } while (this.isLinkWithIdentExist(ident).ok());

        LinkServiceInput input = LinkServiceInput.builder(longLink).customIdent(ident).build();
        OperationResult createLinkResult = this.createLink(input);
        if (createLinkResult.ok()) {
            //create short link
            final String shortLink = appUtils.getShortUrl() + "/" + ident;
            return OperationResult.success().addPayload(shortLink);
        } else {
            return createLinkResult;
        }
    }

    private OperationResult validateIdent(final String ident) {
        String validatedIdent;
        boolean isUsersIdentValid = ident.matches(IdentGenerator.VALID_IDENT_PATTERN);
        if (!isUsersIdentValid) {
            log.info("{} User Ident '{}' is not valid", TAG, ident);
            return OperationResult.malformedInput().withMessage(OP_MALFORMED_IDENT);
        }
        if (isIdentAlreadyExists(ident)) {
            log.info("{} User Ident '{}' already exists", TAG, ident);
            log.debug("{} Conflicting ident: {}", TAG, ident);
            return OperationResult.conflict();
        }
        validatedIdent = ident;

        return OperationResult.success().addPayload(validatedIdent);
    }

    private OperationResult validateUrl(final String link) {
        if (StringUtils.isBlank(link)) {
            log.info("{} Long link is empty", TAG);
            return OperationResult.malformedInput().withMessage(OP_MALFORMED_URL);
        }
        String urlToStore;
        urlToStore = UrlUtils.normalizeUrl(link);
        String messageFromExtraValidator = UrlExtraValidator.isUrlValid(urlToStore);
        switch (messageFromExtraValidator) {
            case UrlExtraValidator.VALID:
                return OperationResult.success().addPayload(link);
            case UrlExtraValidator.LOCAL_URL_NOT_ALLOWED:
                log.info("{} {} is not allowed", TAG, link);
                return OperationResult.banned().withMessage(OP_LOCAL_URL_BANNED);
            case UrlExtraValidator.URL_NOT_VALID:
            default:
                log.info("{} not valid URL: {}", TAG, messageFromExtraValidator);
                return OperationResult.malformedInput().withMessage(OP_MALFORMED_URL);
        }
    }

    private boolean isIdentAlreadyExists(final String ident) {
        OperationResult result = isLinkWithIdentExist(ident);
        boolean isRouteExists = endpointsListener.isRouteExists("/" + ident);
        return result.ok() || isRouteExists;
    }

    private String generateIdent() {
        String ident;
        do {
            ident = IdentGenerator.generateNewIdent();
        } while (isIdentAlreadyExists(ident));
        return ident;
    }
}

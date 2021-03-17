package io.kyberorg.yalsee.services;

import io.kyberorg.yalsee.models.Link;
import io.kyberorg.yalsee.models.dao.LinkRepo;
import io.kyberorg.yalsee.result.GetResult;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.result.StoreResult;
import lombok.extern.slf4j.Slf4j;
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
@Service
public class LinkService {
    private static final String TAG = "[" + LinkService.class.getSimpleName() + "]";
    private final LinkRepo repo;

    /**
     * Constructor for Spring autowiring.
     *
     * @param repo object for communicating with DB
     */
    public LinkService(final LinkRepo repo) {
        this.repo = repo;
    }

    /**
     * Provides stored link by its ident.
     *
     * @param ident string with ident to search against
     * @return search result
     */
    public GetResult getLink(final String ident) {
        Optional<Link> result;
        try {
            result = repo.findSingleByIdent(ident);
        } catch (DataAccessResourceFailureException e) {
            return new GetResult.DatabaseDown().withException(e);
        } catch (Exception e) {
            return new GetResult.Fail().withException(e);
        }

        return result.<GetResult>map(link -> new GetResult.Success(link.getLink()))
                .orElseGet(GetResult.NotFound::new);
    }

    /**
     * Stores new link into DB.
     *
     * @param ident string with part that identifies  short link
     * @param link  string with long URL
     * @return store result
     */
    public StoreResult storeNew(final String ident, final String link) {
        Link linkObject = Link.create(ident, link);
        try {
            repo.save(linkObject);
            return new StoreResult.Success();
        } catch (CannotCreateTransactionException e) {
            return new StoreResult.DatabaseDown().withException(e);
        } catch (Exception e) {
            log.error("{} Exception on storing new {}", TAG, Link.class.getSimpleName());
            log.debug("", e);
            return new StoreResult.Fail("Failed to add new record");
        }
    }

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
}

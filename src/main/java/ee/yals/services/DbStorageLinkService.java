package ee.yals.services;

import ee.yals.models.Link;
import ee.yals.models.dao.LinkRepo;
import ee.yals.result.StoreResult;
import ee.yals.result.GetResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Service, which interacts with database
 *
 * @since 2.0
 */
@Qualifier("dbStorage")
@Component
public class DbStorageLinkService implements LinkService {

    private static final Logger LOGGER = Logger.getLogger(DbStorageLinkService.class);

    @Autowired
    private LinkRepo repo;

    @Override
    public GetResult getLink(String ident) {
        Optional<Link> result = repo.findSingleByIdent(ident);
        return result.<GetResult>map(link -> new GetResult.Success(link.getLink()))
                .orElseGet(GetResult.NotFound::new);
    }

    @Override
    public StoreResult storeNew(String ident, String link) {
        Link linkObject = Link.create(ident, link);
        try {
            repo.save(linkObject);
            return new StoreResult.Success();
        } catch (Exception e) {
            LOGGER.error("Exception on storing new " + Link.class.getSimpleName(), e);
            return new StoreResult.Fail("Failed to add new record");
        }

    }
}

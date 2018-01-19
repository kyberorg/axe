package ee.yals.services;

import ee.yals.models.Link;
import ee.yals.models.User;
import ee.yals.models.dao.LinkDao;
import ee.yals.result.GetResult;
import ee.yals.result.StoreResult;
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
    private LinkDao dao;

    @Override
    public GetResult getLink(String ident) {
        Optional<Link> result = dao.findSingleByIdent(ident);
        return result.<GetResult>map(link -> new GetResult.Success(link.getLink()))
                .orElseGet(GetResult.NotFound::new);
    }

    @Override
    public StoreResult storeNew(String ident, String link, User owner) {
        Link linkObject = Link.store(link).withIdent(ident).andOwner(owner).please();
        try {
            dao.save(linkObject);
            return new StoreResult.Success();
        } catch (Exception e) {
            LOGGER.error("Exception on storing new " + Link.class.getSimpleName(), e);
            return new StoreResult.Fail("Failed to add new link");
        }
    }

}

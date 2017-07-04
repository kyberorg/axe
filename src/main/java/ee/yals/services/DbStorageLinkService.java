package ee.yals.services;

import ee.yals.models.Link;
import ee.yals.models.dao.LinkRepo;
import ee.yals.result.AddResult;
import ee.yals.result.GetResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Service, which interacts with database
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
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
    public AddResult addNew(String ident, String link) {
        Link linkObject = Link.create(ident, link);
        try {
            repo.save(linkObject);
            return new AddResult.Success();
        } catch (Exception e) {
            LOGGER.error("Exception on storing new " + Link.class.getSimpleName(), e);
            return new AddResult.Fail("Failed to add new record");
        }

    }
}

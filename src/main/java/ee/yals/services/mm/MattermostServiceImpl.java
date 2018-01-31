package ee.yals.services.mm;

import ee.yals.core.IdentGenerator;
import ee.yals.models.Link;
import ee.yals.models.dao.LinkRepo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service which handlers requests from {@link ee.yals.controllers.rest.MattermostRestController}
 *
 * @since 2.3
 */
@Service
public class MattermostServiceImpl implements MattermostService {
    private static final Logger LOG = Logger.getLogger(MattermostServiceImpl.class);

    @Autowired
    private LinkRepo repo;

    @Override
    public Link storeLink(String longUrl) {
        Link link = Link.create(IdentGenerator.generateNewIdent(), longUrl);
        Link savedLink;
        try {
            savedLink = repo.save(link);
        } catch (Exception e) {
            LOG.error("Got exception while saving new Link " + link.toString(), e);
            savedLink = null;
        }
        return savedLink;
    }
}

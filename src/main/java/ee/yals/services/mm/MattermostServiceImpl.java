package ee.yals.services.mm;

import ee.yals.core.IdentGenerator;
import ee.yals.models.Link;
import ee.yals.models.dao.LinkRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service which handlers requests from {@link ee.yals.controllers.rest.MattermostRestController}
 *
 * @since 2.3
 */
@Service
@Slf4j
public class MattermostServiceImpl implements MattermostService {

    @Autowired
    private LinkRepo repo;

    @Override
    public Link storeLink(String longUrl) {
        Link link = Link.create(IdentGenerator.generateNewIdent(), longUrl);
        Link savedLink;
        try {
            savedLink = repo.save(link);
        } catch (Exception e) {
            log.error("Got exception while saving new Link " + link.toString(), e);
            savedLink = null;
        }
        return savedLink;
    }
}

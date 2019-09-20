package eu.yals.services.mm;

import eu.yals.controllers.rest.MattermostRestController;
import eu.yals.core.IdentGenerator;
import eu.yals.models.Link;
import eu.yals.models.dao.LinkRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service which handlers requests from {@link MattermostRestController}
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

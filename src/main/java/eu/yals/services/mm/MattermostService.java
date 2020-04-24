package eu.yals.services.mm;

import eu.yals.core.IdentGenerator;
import eu.yals.models.Link;
import eu.yals.models.dao.LinkRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service which handlers requests from {@link eu.yals.controllers.rest.MattermostRestController}.
 *
 * @since 2.3
 */
@Service
@Slf4j
public class MattermostService {

    private final LinkRepo repo;

    /**
     * Constructor for Spring autowiring.
     *
     * @param repo links table Repo object to manipulate with DB
     */
    public MattermostService(final LinkRepo repo) {
        this.repo = repo;
    }

    /**
     * Stores link to DB.
     *
     * @param longUrl string with long link
     * @return stored {@link Link} object with ID from DB.
     */
    public Link storeLink(final String longUrl) {
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

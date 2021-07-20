package io.kyberorg.yalsee.services.mm;

import io.kyberorg.yalsee.api.mm.MattermostRestController;
import io.kyberorg.yalsee.core.IdentGenerator;
import io.kyberorg.yalsee.models.Link;
import io.kyberorg.yalsee.models.dao.LinkRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service which handlers requests from {@link MattermostRestController}.
 *
 * @since 2.3
 */
@Service
@Slf4j
public class MattermostService {
    private static final String TAG = "[" + MattermostService.class.getSimpleName() + "]";
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
            log.error("{}Got exception while saving new Link {}", TAG, link);
            log.debug("", e);
            savedLink = null;
        }
        return savedLink;
    }
}

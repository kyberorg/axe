package eu.yals.services.mm;

import eu.yals.controllers.rest.MattermostRestController;
import eu.yals.models.Link;

/**
 * Service which handlers requests from {@link MattermostRestController}
 *
 * @since 2.3
 */
public interface MattermostService {

    Link storeLink(String longUrl);

}

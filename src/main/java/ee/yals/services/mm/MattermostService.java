package ee.yals.services.mm;

import ee.yals.models.Link;

/**
 * Service which handlers requests from {@link ee.yals.controllers.rest.MattermostRestController}
 *
 * @since 2.3
 */
public interface MattermostService {

    Link storeLink(String longUrl);

}

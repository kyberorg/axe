package ee.yals.services;

import ee.yals.models.User;
import ee.yals.result.GetResult;
import ee.yals.result.StoreResult;

/**
 * Queries for Link storage
 *
 * @since 2.0
 */
public interface LinkService {

    GetResult getLink(String ident);

    StoreResult storeNew(String ident, String link, User owner);
}

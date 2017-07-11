package ee.yals.services;

import ee.yals.result.StoreResult;
import ee.yals.result.GetResult;

/**
 * Queries for Link storage
 *
 * @since 2.0
 */
public interface LinkService {

    GetResult getLink(String ident);

    StoreResult storeNew(String ident, String link);
}

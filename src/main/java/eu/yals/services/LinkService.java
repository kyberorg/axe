package eu.yals.services;

import eu.yals.result.GetResult;
import eu.yals.result.StoreResult;

/**
 * Queries for Link storage
 *
 * @since 2.0
 */
public interface LinkService {

    GetResult getLink(String ident);

    StoreResult storeNew(String ident, String link);
}

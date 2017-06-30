package ee.yals.services;

import ee.yals.result.GetResult;

/**
 * Queries for Link storage
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public interface LinkService {

    GetResult getLink(String ident);
}

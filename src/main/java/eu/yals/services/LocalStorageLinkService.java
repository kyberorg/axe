package eu.yals.services;

import eu.yals.result.GetResult;
import eu.yals.result.StoreResult;
import eu.yals.storage.LinkStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class, which operates with local storage
 *
 * @since 2.0
 */
@Qualifier("localStorage")
@Component
public class LocalStorageLinkService implements LinkService {

    @Autowired
    private LinkStorage storage;

    @Override
    public GetResult getLink(String ident) {
        String link = storage.find(ident);
        return link.equals(LinkStorage.LINK_NOT_FOUND) ? new GetResult.NotFound() : new GetResult.Success(link);
    }

    @Override
    public StoreResult storeNew(String ident, String link) {
        storage.save(ident, link);
        return new StoreResult.Success();
    }
}

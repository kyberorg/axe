package eu.yals.services.overall;

import eu.yals.models.dao.LinkRepo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Service which queries from DB about totals
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 2.1
 */
@Qualifier("dbOverallService")
@Component
public class DbStorageOverallService implements OverallService {

    private final LinkRepo repo;

    public DbStorageOverallService(LinkRepo repo) {
        this.repo = repo;
    }

    @Override
    public long numberOfStoredLinks() {
        if(Objects.nonNull(repo)){
            return repo.count();
        } else {
            return 0;
        }
    }
}

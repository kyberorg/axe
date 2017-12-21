package ee.yals.services.overall;

import ee.yals.models.dao.LinkRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * ervice which queries from DB about totals
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 2.1
 */
@Qualifier("dbOverallService")
@Component
public class DbStorageOverallService implements OverallService {

    @Autowired
    private LinkRepo repo;

    @Override
    public long numberOfStoredLinks() {
        if(Objects.nonNull(repo)){
            return repo.count();
        } else {
            return 0;
        }
    }
}

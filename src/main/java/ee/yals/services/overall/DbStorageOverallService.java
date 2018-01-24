package ee.yals.services.overall;

import ee.yals.models.dao.LinkDao;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private LinkDao dao;

    @Override
    public long numberOfStoredLinks() {
        return Objects.nonNull(dao) ? dao.count() : 0;
    }
}

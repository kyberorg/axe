package pm.axe.services.overall;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pm.axe.db.dao.LinkDao;

import java.util.Objects;

/**
 * Service which queries from DB about totals.
 *
 * @since 2.1
 */
@RequiredArgsConstructor
@Service
public class OverallService {

    private final LinkDao repo;

    /**
     * Retrieves number of stored links.
     *
     * @return long number with qty of stored links in DB
     */
    public long numberOfStoredLinks() {
        if (Objects.nonNull(repo)) {
            return repo.count();
        } else {
            return 0;
        }
    }
}

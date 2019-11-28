package eu.yals.services.overall;

import eu.yals.models.dao.LinkRepo;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Service which queries from DB about totals
 *
 * @since 2.1
 */
@Service
public class OverallService {

    private final LinkRepo repo;

    public OverallService(LinkRepo repo) {
        this.repo = repo;
    }

    public long numberOfStoredLinks() {
        if (Objects.nonNull(repo)) {
            return repo.count();
        } else {
            return 0;
        }
    }
}

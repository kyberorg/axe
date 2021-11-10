package io.kyberorg.yalsee.services.overall;

import io.kyberorg.yalsee.models.dao.LinkRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Service which queries from DB about totals.
 *
 * @since 2.1
 */
@AllArgsConstructor
@Service
public class OverallService {

    private final LinkRepo repo;

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

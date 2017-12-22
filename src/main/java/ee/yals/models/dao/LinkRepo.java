package ee.yals.models.dao;

import ee.yals.models.Link;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Link DTO
 *
 * @since 2.0
 */
public interface LinkRepo extends Repository<Link, Long> {
    Optional<Link> findSingleByIdent(String linkIdent);
    Link save(Link linkToSave);
    List<Link> findAll();
    long count();
}

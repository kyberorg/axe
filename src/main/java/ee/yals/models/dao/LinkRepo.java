package ee.yals.models.dao;

import ee.yals.models.Link;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Link DTO
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public interface LinkRepo extends Repository<Link, Long> {
    Optional<Link> findSingleByIdent(String linkIdent);
    Link save(Link linkToSave);
    List<Link> findAll();
}

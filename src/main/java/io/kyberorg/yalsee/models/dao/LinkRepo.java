package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.Link;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Link DTO.
 *
 * @since 2.0
 */
public interface LinkRepo extends Repository<Link, Long> {
    /**
     * Find link by its ident.
     *
     * @param linkIdent string with ident to search against
     * @return {@link Optional} with or without link
     */
    Optional<Link> findSingleByIdent(String linkIdent);

    /**
     * Saves link to DB.
     *
     * @param linkToSave {@link Link} object with filled fields
     * @return same {@link Link} object, but enriched with ID field
     */
    Link save(Link linkToSave);

    /**
     * Gets all links from DB.
     *
     * @return list of all saved {@link Link} objects
     */
    List<Link> findAll();

    /**
     * Counts how many links are saved.
     *
     * @return number of saved links
     */
    long count();

    /**
     * Deletes link from DB.
     *
     * @param linkToDelete {@link Link} object
     */
    void delete(Link linkToDelete);
}

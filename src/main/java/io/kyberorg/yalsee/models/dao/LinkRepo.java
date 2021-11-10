package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.Link;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * Link DTO.
 *
 * @since 2.0
 */
@org.springframework.stereotype.Repository
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
     * Counts how many links are saved.
     *
     * @return number of saved links
     */
    long count();

    /**
     * Counts how many links are saved under give ident.
     *
     * @param linkIdent string with ident to search against
     * @return number of found links
     */
    long countByIdent(String linkIdent);

    /**
     * Deletes link from DB.
     *
     * @param linkToDelete {@link Link} object
     */
    void delete(Link linkToDelete);
}

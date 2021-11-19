package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.Link;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Link DTO.
 *
 * @since 2.0
 */
@org.springframework.stereotype.Repository
public interface LinkRepo extends CrudRepository<Link, Long> {

    /**
     * Find link by its ident.
     *
     * @param linkIdent string with ident to search against
     * @return {@link Optional} with or without link
     */
    Optional<Link> findSingleByIdent(String linkIdent);


    /**
     * Counts how many links are saved under give ident.
     *
     * @param linkIdent string with ident to search against
     * @return number of found links
     */
    long countByIdent(String linkIdent);

}

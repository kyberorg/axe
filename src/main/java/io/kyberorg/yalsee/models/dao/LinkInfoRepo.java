package io.kyberorg.yalsee.models.dao;

import io.kyberorg.yalsee.models.LinkInfo;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * DAO for {@link LinkInfo} model.
 *
 * @since 3.2
 */
@org.springframework.stereotype.Repository
public interface LinkInfoRepo extends Repository<LinkInfo, Long> {

    List<LinkInfo> findBySession(String sessionId);

    /**
     * Saves  info to DB.
     *
     * @param linkInfoObject {@link LinkInfo} object with filled fields
     * @return same {@link LinkInfo} object, but enriched with ID field
     */
    LinkInfo save(LinkInfo linkInfoObject);

    /**
     * Number of link info records.
     *
     * @return int with number of links stored
     */
    int count();

    LinkInfo findSingleById(long id);

    LinkInfo findSingleByIdent(String ident);

    long countByIdent(String ident);

    void delete(LinkInfo linkInfoToDelete);
}

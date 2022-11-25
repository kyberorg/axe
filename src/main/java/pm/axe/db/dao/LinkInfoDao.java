package pm.axe.db.dao;

import org.springframework.stereotype.Repository;
import pm.axe.db.dao.base.TimeAwareCrudDao;
import pm.axe.db.models.LinkInfo;

import java.util.List;

/**
 * DAO for {@link LinkInfo} model.
 *
 * @since 3.2
 */
@Repository
public interface LinkInfoDao extends TimeAwareCrudDao<LinkInfo, Long> {

    /**
     * Finds {@link LinkInfo} objects stored within given session.
     *
     * @param sessionId string with Session identifier to search against.
     * @return list of {@link LinkInfo} records or empty list if nothing found.
     */
    List<LinkInfo> findBySession(String sessionId);

    /**
     * Finds single record by its id.
     *
     * @param id record identifier in database
     * @return found {@link LinkInfo} object or {@code null} if nothing found
     */
    LinkInfo findSingleById(long id);

    /**
     * Finds single record by its ident.
     *
     * @param ident non-empty string with ident to search against.
     * @return found {@link LinkInfo} object or {@code null} if nothing found
     */
    LinkInfo findSingleByIdent(String ident);

    /**
     * Counts records stored under given ident.
     *
     * @param ident non-empty string with ident to search against.
     * @return number of found records. Normally it should be only 1 record or 0 records if nothing found.
     */
    long countByIdent(String ident);
}

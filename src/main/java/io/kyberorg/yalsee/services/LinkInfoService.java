package io.kyberorg.yalsee.services;

import io.kyberorg.yalsee.models.Link;
import io.kyberorg.yalsee.models.LinkInfo;
import io.kyberorg.yalsee.models.dao.LinkInfoRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service, which interacts with database to store/retrieve link info.
 *
 * @since 2.0
 */
@Slf4j
@Service
public class LinkInfoService {
    private final LinkInfoRepo repo;

    /**
     * Constructor for Spring autowiring.
     *
     * @param linkInfoRepo object for storing link info to DB
     */
    public LinkInfoService(final LinkInfoRepo linkInfoRepo) {
        this.repo = linkInfoRepo;
    }

    /**
     * Creates {@link LinkInfo} without session info.
     *
     * @param ident string with part that identifies short link
     */
    public void createLinkInfo(final String ident) {
        createLinkInfo(ident, null);
    }

    /**
     * Creates {@link LinkInfo} with session info.
     *
     * @param ident   string with part that identifies short link
     * @param session string with session ID
     */
    public void createLinkInfo(final String ident, final String session) {
        LinkInfo linkInfo;
        if (linkInfoExistsForIdent(ident)) {
            update(repo.findSingleByIdent(ident));
            return;
        } else {
            linkInfo = new LinkInfo();
        }

        linkInfo.setIdent(ident);
        if (session != null) {
            linkInfo.setSession(session);
        }
        linkInfo.setCreated(Timestamp.from(Instant.now()));
        linkInfo.setUpdated(Timestamp.from(Instant.now()));

        repo.save(linkInfo);
    }

    /**
     * Deletes {@link LinkInfo}  by its ident.
     *
     * @param ident string with part that identifies short link
     */
    public void deleteLinkInfo(final String ident) {
        if (linkInfoExistsForIdent(ident)) {
            LinkInfo linkInfoToDelete = repo.findSingleByIdent(ident);
            repo.delete(linkInfoToDelete);
        }
    }

    /**
     * Retrieves all {@link LinkInfo} objects stored under given session.
     *
     * @param sessionID string with Session identifier.
     * @return list of {@link LinkInfo} records or empty list if nothing found.
     */
    public List<LinkInfo> getAllRecordWithSession(final String sessionID) {
        return repo.findBySession(sessionID);
    }

    /**
     * Retrieves {@link LinkInfo} object by its id in database.
     *
     * @param id record id in database
     * @return {@link Optional} of {@link LinkInfo}.
     */
    public Optional<LinkInfo> getLinkInfoById(final long id) {
        return Optional.of(repo.findSingleById(id));
    }

    /**
     * Retrieves {@link LinkInfo} for given {@link Link}. Match is done by {@code ident}.
     *
     * @param link valid {@link Link}
     * @return {@link Optional} of {@link LinkInfo}.
     */
    public Optional<LinkInfo> getLinkInfoByLink(final Link link) {
        if (link == null) return Optional.empty();
        LinkInfo linkInfo = repo.findSingleByIdent(link.getIdent());
        return Optional.ofNullable(linkInfo);
    }

    /**
     * Updates {@link LinkInfo} record.
     *
     * @param updatedLinkInfo {@link LinkInfo} object with updated fields.
     */
    public void update(final LinkInfo updatedLinkInfo) {
        updatedLinkInfo.setUpdated(Timestamp.from(Instant.now()));
        repo.save(updatedLinkInfo);
    }

    private boolean linkInfoExistsForIdent(final String ident) {
        return repo.countByIdent(ident) > 0;
    }
}

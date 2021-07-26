package io.kyberorg.yalsee.services.internal;

import io.kyberorg.yalsee.models.LinkInfo;
import io.kyberorg.yalsee.models.dao.LinkInfoRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LinkInfoService {
    private final LinkInfoRepo repo;

    /**
     * Constructor for Spring autowiring.
     *
     * @param linkInfoRepo  object for storing link info to DB
     */
    public LinkInfoService(final LinkInfoRepo linkInfoRepo) {
        this.repo = linkInfoRepo;
    }

    /**
     * Creates link info without session info.
     *
     * @param ident   string with part that identifies short link
     */
    public void createLinkInfo(final String ident) {
        createLinkInfo(ident, null);
    }

    /**
     *Creates link info with session info.
     *
     * @param ident   string with part that identifies short link
     * @param session string with session ID
     *
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

    public void deleteLinkInfo(final String ident) {
        if (linkInfoExistsForIdent(ident)) {
            LinkInfo linkInfoToDelete = repo.findSingleByIdent(ident);
            repo.delete(linkInfoToDelete);
        }
    }

    public List<LinkInfo> getAllRecordWithSession(String sessionID) {
        return repo.findBySession(sessionID);
    }

    public Optional<LinkInfo> getLinkInfoById(long id) {
        return Optional.of(repo.findSingleById(id));
    }

    public void update(LinkInfo updatedLinkInfo) {
        updatedLinkInfo.setUpdated(Timestamp.from(Instant.now()));
        repo.save(updatedLinkInfo);
    }

    private boolean linkInfoExistsForIdent(final String ident) {
        return repo.countByIdent(ident) > 0;
    }
}

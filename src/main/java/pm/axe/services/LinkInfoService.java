package pm.axe.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pm.axe.db.dao.LinkInfoDao;
import pm.axe.db.models.Link;
import pm.axe.db.models.LinkInfo;
import pm.axe.db.models.User;
import pm.axe.services.user.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service, which interacts with database to store/retrieve link info.
 *
 * @since 2.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LinkInfoService {
    private final LinkInfoDao repo;
    private final UserService userService;

    /**
     * Creates {@link LinkInfo} with session info.
     *
     * @param ident       string with part that identifies short link
     * @param session     string with session ID, omitted if {@code null} or empty
     * @param description string with link description, omitted if {@code null} or empty
     * @param owner       link's owner
     */
    public void createLinkInfo(final String ident, final String session, final String description, final User owner) {
        LinkInfo linkInfo;
        if (linkInfoExistsForIdent(ident)) {
            update(repo.findSingleByIdent(ident));
            return;
        } else {
            linkInfo = new LinkInfo();
        }

        linkInfo.setIdent(ident);
        if (StringUtils.isNotBlank(description)) {
            linkInfo.setDescription(description);
        }
        if (StringUtils.isNotBlank(session)) {
            linkInfo.setSession(session);
        }

        linkInfo.setOwner(Objects.requireNonNullElseGet(owner, userService::getDefaultUser));
        repo.update(linkInfo);
    }

    /**
     * Deletes {@link LinkInfo} by its ident.
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
        repo.update(updatedLinkInfo);
    }

    private boolean linkInfoExistsForIdent(final String ident) {
        return repo.countByIdent(ident) > 0;
    }
}

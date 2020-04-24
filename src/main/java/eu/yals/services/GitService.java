package eu.yals.services;

import eu.yals.constants.App;
import eu.yals.utils.git.GitInfo;
import eu.yals.utils.git.MavenGitInfo;
import eu.yals.utils.git.NoGitInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Service, that provides source of git information (like commit, tag, jne).
 *
 * @since 2.5
 */
@Slf4j
@Service
public class GitService {
    private static final String TAG = "[" + GitService.class.getSimpleName() + "]";
    private final MavenGitInfo mavenGitInfo;
    private final NoGitInfo noGitInfo;

    @Getter
    private final String latestCommit;
    @Getter
    private final String latestTag;

    /**
     * Constructor for Spring autowiring.
     *
     * @param mavenGitInfo information from pom.xml
     * @param noGitInfo    fallback {@link GitInfo} implementation
     */
    public GitService(final MavenGitInfo mavenGitInfo, final NoGitInfo noGitInfo) {
        this.mavenGitInfo = mavenGitInfo;
        this.noGitInfo = noGitInfo;

        latestCommit = this.getGitInfoSource().getLatestCommitHash().trim();
        latestTag = this.getGitInfoSource().getLatestTag().trim();
    }

    private GitInfo getGitInfoSource() {
        if (mavenGitInfo.isApplicable()) {
            log.trace("{} Will return {}", TAG, MavenGitInfo.class.getSimpleName());
            return mavenGitInfo;
        } else {
            log.trace("{} Will return {}", TAG, NoGitInfo.class.getSimpleName());
            return noGitInfo;
        }
    }

    /**
     * Verifies if commit hash present or not.
     *
     * @return true - if object has commit hash, else - false
     */
    public boolean commitPresent() {
        return (!latestCommit.equals(App.NO_VALUE) && StringUtils.isNotBlank(latestCommit));
    }

    /**
     * Verifies if latest application tag present or not.
     *
     * @return true if present, false - if not.
     */
    public boolean tagPresent() {
        return (!latestTag.equals(App.NO_VALUE) && StringUtils.isNotBlank(latestTag));
    }
}

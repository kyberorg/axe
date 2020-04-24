package eu.yals.utils.git;

import eu.yals.constants.App;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Provides git information (maven way).
 *
 * @since 2.0
 */
@Component
public class MavenGitInfo implements GitInfo {
    private final GitRepoState gitRepoState;

    /**
     * Creates {@link MavenGitInfo} object.
     *
     * @param gitRepoState build time info
     */
    public MavenGitInfo(final GitRepoState gitRepoState) {
        this.gitRepoState = gitRepoState;
    }

    @Override
    public boolean isApplicable() {
        return Objects.nonNull(gitRepoState) && gitRepoState.hasValues();
    }

    @Override
    public String getLatestCommitHash() {
        return StringUtils.isNotBlank(gitRepoState.getCommitIdAbbrev())
                ? gitRepoState.getCommitIdAbbrev() : App.NO_VALUE;
    }

    @Override
    public String getLatestTag() {
        //we use here version from maven, because it is more stable then git tag
        return StringUtils.isNotBlank(gitRepoState.getBuildVersion()) ? gitRepoState.getBuildVersion() : App.NO_VALUE;
    }
}

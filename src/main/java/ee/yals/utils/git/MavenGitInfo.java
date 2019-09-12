package ee.yals.utils.git;

import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Provides git information (maven way)
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 2.0
 */
@Component
public class MavenGitInfo extends GitInfo {
    /**
     * No reason for instance. Use {@link GitInfo#getInstance()} instead
     */
    MavenGitInfo() {
    }

    private GitRepoState gitRepoState = GitRepoState.getInstance();

    @Override
    boolean isApplicable() {
        return Objects.nonNull(gitRepoState) && gitRepoState.correctlyInitialized();
    }

    @Override
    public String getLatestCommitHash() {
        return gitRepoState.commitIdAbbrev;
    }

    @Override
    public String getLatestTag() {
        return gitRepoState.buildVersion; //we use here version from maven, because it is more stable then git tag
    }
}

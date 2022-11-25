package pm.axe.utils.git;

import pm.axe.constants.App;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Provides git information (maven way).
 *
 * @since 2.0
 */
@RequiredArgsConstructor
@Component
public class MavenGitInfo implements GitInfo {
    private final GitRepoState gitRepoState;

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
        //we use here version from maven, because it is more stable than git tag
        return StringUtils.isNotBlank(gitRepoState.getBuildVersion()) ? gitRepoState.getBuildVersion() : App.NO_VALUE;
    }
}
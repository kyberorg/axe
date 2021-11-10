package io.kyberorg.yalsee.utils.git;

import io.kyberorg.yalsee.constants.App;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Provides git information (maven way).
 *
 * @since 2.0
 */
@AllArgsConstructor
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

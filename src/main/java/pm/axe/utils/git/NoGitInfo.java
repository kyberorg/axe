package pm.axe.utils.git;

import pm.axe.constants.App;
import org.springframework.stereotype.Component;

/**
 * Do nothing git info aka fallback, when other methods failed to apply.
 * Always responds with {@link App#NO_VALUE}
 *
 * @since 2.0
 */
@Component
public class NoGitInfo implements GitInfo {

    @Override
    public boolean isApplicable() {
        return true;
    }

    @Override
    public String getLatestCommitHash() {
        return App.NO_VALUE;
    }

    @Override
    public String getLatestTag() {
        return App.NO_VALUE;
    }
}

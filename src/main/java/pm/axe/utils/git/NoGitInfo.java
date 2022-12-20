package pm.axe.utils.git;

import org.springframework.stereotype.Component;
import pm.axe.Axe;

/**
 * Do nothing git info aka fallback, when other methods failed to apply.
 * Always responds with {@link Axe.C#NO_VALUE}
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
        return Axe.C.NO_VALUE;
    }

    @Override
    public String getLatestTag() {
        return Axe.C.NO_VALUE;
    }
}

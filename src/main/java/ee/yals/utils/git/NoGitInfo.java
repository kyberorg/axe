package ee.yals.utils.git;

import org.springframework.stereotype.Component;

/**
 * Do nothing git info aka fallback, when other methods failed to apply.
 * Always responds with {@link GitInfo#NOTHING_FOUND_MARKER}
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 2.0
 */
@Component
public class NoGitInfo extends GitInfo {
    /**
     * No reason for instance. Use {@link GitInfo#getInstance()} instead
     */
    NoGitInfo() {
    }

    @Override
    boolean isApplicable() {
        return true;
    }

    @Override
    public String getLatestCommitHash() {
        return GitInfo.NOTHING_FOUND_MARKER;
    }

    @Override
    public String getLatestTag() {
        return GitInfo.NOTHING_FOUND_MARKER;
    }
}

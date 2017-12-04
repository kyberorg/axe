package ee.yals.utils.git;

import org.springframework.stereotype.Component;

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
    MavenGitInfo() { }

    @Override
    boolean isApplicable() {
        //TODO implement
        return false;
    }

    @Override
    public String getLatestCommitHash() {
        //TODO implement
        return GitInfo.NOTHING_FOUND_MARKER;
    }

    @Override
    public String getLatestTag() {
        //TODO implement
        return GitInfo.NOTHING_FOUND_MARKER;
    }
}

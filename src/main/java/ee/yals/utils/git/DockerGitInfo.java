package ee.yals.utils.git;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Provides git information (docker way)
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 2.0
 */
@Component
public class DockerGitInfo extends GitInfo {
    private static final Logger Log = Logger.getLogger(GitInfo.class);
    private static final String COMMIT_FILE = "/app/COMMIT";
    private static final String TAG_FILE = "/app/TAG/";

    /**
     * No reason for instance. Use {@link GitInfo#getInstance()} instead
     */
    DockerGitInfo() {
    }

    @Override
    protected boolean isApplicable() {
        boolean isCommitFileExists = new File(COMMIT_FILE).exists();
        boolean isTagFileExists = new File(TAG_FILE).exists();

        return isCommitFileExists && isTagFileExists;
    }

    @Override
    public String getLatestCommitHash() {
        return findLatestCommitHash();
    }

    @Override
    public String getLatestTag() {
        return findLatestTag();
    }

    private String findLatestCommitHash() {
        try {
            String lastCommit = FileUtils.readFileToString(new File(COMMIT_FILE), Charset.defaultCharset()).trim();
            return !lastCommit.isEmpty() ? lastCommit : GitInfo.NOTHING_FOUND_MARKER;
        } catch (Exception e) {
            Log.warn("Exception while getting latest commit hash", e);
            return "";
        }
    }

    private String findLatestTag() {
        try {
            String lastTag = FileUtils.readFileToString(new File(TAG_FILE), Charset.defaultCharset()).trim();
            return !lastTag.isEmpty() ? lastTag : GitInfo.NOTHING_FOUND_MARKER;
        } catch (Exception e) {
            Log.warn("Exception while getting latest tag", e);
            return "";
        }
    }
}

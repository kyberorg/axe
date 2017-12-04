package ee.yals.utils;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Provides correct application version
 *
 * @since 1.0
 * @deprecated use {@link ee.yals.utils.git.GitInfo}
 */

@Deprecated()
public class OldGitInfo {
    private static final Logger Log = Logger.getLogger(OldGitInfo.class);
    public static final String REPOSITORY = "https://github.com/yadevee/yals/commit/";
    private static final String COMMIT_FILE = "/app/COMMIT";
    private static final String TAG_FILE = "/app/TAG/";

    private String latestCommit;
    private String latestTag;

    public OldGitInfo() {
        latestCommit = findLatestCommitHash();
        latestTag = findLatestTag();
    }

    public String getLatestCommitHash() {
        return latestCommit;
    }

    public String getLatestTag() {
        return latestTag;
    }

    private String findLatestCommitHash() {
        try {
            String lastCommit = FileUtils.readFileToString(new File(COMMIT_FILE), Charset.defaultCharset()).trim();
            return !lastCommit.isEmpty() ? lastCommit : "";
        } catch (Exception e) {
            Log.warn("Exception while getting latest commit hash", e);
            return "";
        }
    }

    private String findLatestTag() {
        try {
            String lastTag = FileUtils.readFileToString(new File(TAG_FILE), Charset.defaultCharset()).trim();
            return !lastTag.isEmpty() ? lastTag : "";
        } catch (Exception e) {
            Log.warn("Exception while getting latest tag", e);
            return "";
        }
    }
}
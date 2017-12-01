package ee.yals.utils;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Provides correct application version
 *
 * @since 1.0
 */
@Component
public class GitInfo {
    private static final Logger Log = Logger.getLogger(GitInfo.class);
    public static final String REPOSITORY = "https://github.com/yadevee/yals/commit/";

    private String latestCommit;
    private String latestTag;

    public GitInfo() {
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
        debug();
        try {
            String lastCommit = FileUtils.readFileToString(new File("./COMMIT"), Charset.defaultCharset()).trim();
            return !lastCommit.isEmpty() ? lastCommit : "";
        } catch (Exception e) {
            Log.warn("Exception while getting latest commit hash", e);
            return "";
        }
    }

    private String findLatestTag() {
        try {
            String lastTag = FileUtils.readFileToString(new File("./TAG"), Charset.defaultCharset()).trim();
            return !lastTag.isEmpty() ? lastTag : "";
        } catch (Exception e) {
            Log.warn("Exception while getting latest tag", e);
            return "";
        }
    }

    private void debug(){
        String pwd = new File(".").getAbsolutePath();
        Log.warn("PWD: " + pwd);
    }
}
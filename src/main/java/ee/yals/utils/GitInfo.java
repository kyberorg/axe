package ee.yals.utils;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Provides correct application version
 *
 * @since 1.0
 */
@Component
public class GitInfo {
    private static final Logger Log = Logger.getLogger(GitInfo.class);
    public static final String REPOSITORY = "https://bitbucket.org/virtalab/yals-spring/commits/";

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
        try {
            String ref = FileUtils.readFileToString(new File("./.git/HEAD"), Charset.defaultCharset()).replace("ref:", "").trim();
            String lastCommit = FileUtils.readFileToString(new File(String.format("./.git/%s", ref)), Charset.defaultCharset());
            return (Objects.nonNull(lastCommit) && !lastCommit.isEmpty() ? lastCommit : "");
        } catch (Exception e) {
            Log.warn("Exception while getting latest commit hash", e);
            return "";
        }
    }

    private String findLatestTag() {
        try {
            File tagsDir = new File("./.git/refs/tags");
            if (tagsDir.isDirectory() && tagsDir.canRead()) {
                File[] tags = tagsDir.listFiles();
                if (tags == null || tags.length == 0) {
                    Log.warn("No tags in git found");
                    return "";
                }
                List<File> tagsList = Arrays.asList(tags);
                tagsList.sort((f1, f2) -> {
                    if (f2.lastModified() > f1.lastModified()) {
                        return 1;
                    } else if (f2.lastModified() == f1.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }
                });
                logTagsList(tagsList);
                String lastTag = tagsList.get(0).getName();
                Log.info(String.format("Application version is: %s", lastTag.replaceAll("[^\\d.]", "")));
                return lastTag;
            } else {
                Log.warn("Git directory is not readable");
                return "";
            }
        } catch (Exception e) {
            Log.warn("Exception while getting latest tag", e);
            return "";
        }
    }

    private void logTagsList(List<File> tags) {
        if (Log.isDebugEnabled()) {
            Log.debug("Tags found");
            for (File tag : tags) {
                Log.debug(String.format("Tag: %s, Date modified: %s", tag.getName(), tag.lastModified()));
            }
        }
    }
}
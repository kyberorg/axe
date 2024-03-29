package pm.axe.utils.git;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Object with properties about the git repository state at build time.
 * This information is supplied by my plugin - <b>pl.project13.maven.git-commit-id-plugin</b>
 * Following params are supported:
 * <p>
 * String tags;                    // =${git.tags} // comma separated tag names
 * String branch;                  // =${git.branch}
 * String dirty;                   // =${git.dirty}
 * String remoteOriginUrl;         // =${git.remote.origin.url}
 * String commitId;                // =${git.commit.id.full} OR ${git.commit.id}
 * String commitIdAbbrev;          // =${git.commit.id.abbrev}
 * String describe;                // =${git.commit.id.describe}
 * String describeShort;           // =${git.commit.id.describe-short}
 * String commitUserName;          // =${git.commit.user.name}
 * String commitUserEmail;         // =${git.commit.user.email}
 * String commitMessageFull;       // =${git.commit.message.full}
 * String commitMessageShort;      // =${git.commit.message.short}
 * String commitTime;              // =${git.commit.time}
 * String closestTagName;          // =${git.closest.tag.name}
 * String closestTagCommitCount;   // =${git.closest.tag.commit.count}
 * String buildUserName;           // =${git.build.user.name}
 * String buildUserEmail;          // =${git.build.user.email}
 * String buildTime;               // =${git.build.time}
 * String buildHost;               // =${git.build.host}
 * String buildVersion;             // =${git.build.version}
 *
 * @since 2.0
 */
@Slf4j
@Data
@Component
public class GitRepoState {
    private static final String TAG = "[" + GitRepoState.class.getSimpleName() + "]";
    private static final String GIT_PROPERTIES_FILE = "git.properties";

    private final Properties gitProperties = new Properties();

    private String commitIdAbbrev;          // =${git.commit.id.abbrev}
    private String buildVersion;             // =${git.build.version}
    private String branch;                  //=${git.branch}
    private String buildHost;               //=${git.build.host}

    /**
     * Creates {@link GitRepoState} object.
     */
    public GitRepoState() {
        init();
    }

    /**
     * Controls if object populated values or not.
     *
     * @return true if values are correctly populated, false if not
     */
    public boolean hasValues() {
        return !gitProperties.isEmpty();
    }

    private void init() {
        if (this.getClass().getClassLoader() == null) {
            log.error("{} '{}': no such file. Did you run 'mvn package' ? (Note: ignore, if profile is 'default')",
                    TAG, GIT_PROPERTIES_FILE);
            this.gitProperties.clear();
            return;
        }

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(GIT_PROPERTIES_FILE);
        if (is == null) {
            log.error("{} '{}': no such file. Did you run 'mvn package' ? (Note: ignore, if profile is 'default')",
                    TAG, GIT_PROPERTIES_FILE);
            this.gitProperties.clear();
            return;
        }
        try {
            this.gitProperties.load(is);
            log.trace("{} {}: parsed info from file: {}", TAG, GitRepoState.class.getSimpleName(), GIT_PROPERTIES_FILE);
            this.publishFromProperties();
        } catch (IOException ioe) {
            log.error("{} Failed to init {}", TAG, GitRepoState.class.getSimpleName());
            log.debug("", ioe);
            this.gitProperties.clear();
        }
    }

    private void publishFromProperties() {
        this.commitIdAbbrev = String.valueOf(gitProperties.get("git.commit.id.abbrev"));
        this.buildVersion = String.valueOf(gitProperties.get("git.build.version"));
        this.branch = String.valueOf(gitProperties.get("git.branch"));
        this.buildHost = String.valueOf(gitProperties.get("git.build.host"));
    }

}

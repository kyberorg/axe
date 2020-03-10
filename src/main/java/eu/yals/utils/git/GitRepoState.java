package eu.yals.utils.git;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
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
    private static final String GIT_PROPERTIES_FILE = "git.properties";

    private final Properties gitProperties = new Properties();

    String commitIdAbbrev;          // =${git.commit.id.abbrev}
    String buildVersion;             // =${git.build.version}
    String branch;                  //=${git.branch}
    String buildHost;               //=${git.build.host}

    public GitRepoState() {
        init();
    }

    public boolean hasValues() {
        return !gitProperties.isEmpty();
    }

    private void init() {
        if(this.getClass().getClassLoader() == null) {
            log.error("'{}': no such file. Did you run 'mvn package' ? (Note: ignore, if profile is 'local')",
                    GIT_PROPERTIES_FILE);
            this.gitProperties.clear();
            return;
        }

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(GIT_PROPERTIES_FILE);
        try {
            this.gitProperties.load(is);
            log.trace("{}: parsed info from file: {}", GitRepoState.class.getSimpleName(), GIT_PROPERTIES_FILE);
            this.publishFromProperties();
        } catch (IOException ioe) {
            log.error("Failed to init " + GitRepoState.class.getSimpleName(), ioe);
            this.gitProperties.clear();
        } catch (NullPointerException npe) {
            log.error("'{}': no such file. Did you run 'mvn package' ? (Note: ignore, if profile is 'local')",
                    GIT_PROPERTIES_FILE);
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

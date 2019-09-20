package eu.yals.services;

import eu.yals.utils.git.GitInfo;
import eu.yals.utils.git.MavenGitInfo;
import eu.yals.utils.git.NoGitInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service, that provides source of git information (like commit, tag, jne)
 *
 * @since 2.5
 */
@Slf4j
@Service
public class GitService {
    private static final String TAG = "[Git Service]";
    private final MavenGitInfo mavenGitInfo;
    private final NoGitInfo noGitInfo;

    public GitService(MavenGitInfo mavenGitInfo, NoGitInfo noGitInfo) {
        this.mavenGitInfo = mavenGitInfo;
        this.noGitInfo = noGitInfo;
    }

    public GitInfo getGitInfoSource() {
        if (mavenGitInfo.isApplicable()) {
            log.trace("{} Will return {}", TAG, MavenGitInfo.class.getSimpleName());
            return mavenGitInfo;
        } else {
            log.trace("{} Will return {}", TAG, NoGitInfo.class.getSimpleName());
            return noGitInfo;
        }
    }
}

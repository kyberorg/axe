package ee.yals.utils.git;

/**
 * Provides correct application version
 *
 * @author Alexander Muravya (alexander.muravya@kuehne-nagel.com)
 * @since 1.0
 */
public abstract class GitInfo {
    public static final String REPOSITORY = "https://github.com/yadevee/yals/commit/";
    public static final String NOTHING_FOUND_MARKER = "-";

    public static GitInfo getInstance() {
        MavenGitInfo mavenGitInfo = new MavenGitInfo();
        if (mavenGitInfo.isApplicable()) {
            return mavenGitInfo;
        } else {
            return new NoGitInfo();
        }
    }

    /**
     * No reason for instance. Use {@link #getInstance()} instead
     */
    protected GitInfo() {
    }

    /**
     * Defines if class be used to provide correct git info
     *
     * @return true - if class can be used to provide git information, false - elsewhere
     */
    abstract boolean isApplicable();

    /**
     * Provides hash of latest commit
     *
     * @return string with commit hash or {@link GitInfo#NOTHING_FOUND_MARKER} when not found
     */
    public abstract String getLatestCommitHash();

    /**
     * Provides last tag in git repository
     *
     * @return string with name of latest Git tag or {@link GitInfo#NOTHING_FOUND_MARKER} when not found
     */
    public abstract String getLatestTag();
}

package ee.yals.utils.git;

/**
 * Provides correct application version
 *
 * @since 1.0
 */
public interface GitInfo {
    /**
     * Defines if class be used to provide correct git info
     *
     * @return true - if class can be used to provide git information, false - elsewhere
     */
    boolean isApplicable();

    /**
     * Provides hash of latest commit
     *
     * @return string with commit hash or {@link ee.yals.constants.App#NO_VALUE} when not found
     */
    String getLatestCommitHash();

    /**
     * Provides last tag in git repository
     *
     * @return string with name of latest Git tag or {@link ee.yals.constants.App#NO_VALUE} when not found
     */
    String getLatestTag();
}

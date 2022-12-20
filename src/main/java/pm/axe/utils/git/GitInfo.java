package pm.axe.utils.git;

import pm.axe.Axe;

/**
 * Provides correct application version.
 *
 * @since 1.0
 */
public interface GitInfo {
    /**
     * Defines if class be used to provide correct git info.
     *
     * @return true - if class can be used to provide git information, false - elsewhere
     */
    boolean isApplicable();

    /**
     * Provides hash of the latest commit.
     *
     * @return string with commit hash or {@link Axe.C#NO_VALUE} when not found
     */
    String getLatestCommitHash();

    /**
     * Provides last tag in git repository.
     *
     * @return string with name of the latest Git tag or {@link Axe.C#NO_VALUE} when not found
     */
    String getLatestTag();
}

package pm.axe.mm;

import pm.axe.utils.UrlExtraValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static pm.axe.utils.UrlExtraValidator.VALID;

/**
 * Combination of types present in MM Text ({@link Mattermost#text} ).
 *
 * @since 2.3.1
 */
public final class MattermostArgumentSet {
    public static final MattermostArgumentSet EMPTY_SET = MattermostArgumentSet.emptySet();
    public static final MattermostArgumentSet BROKEN_SET = MattermostArgumentSet.brokenSet();

    private static MattermostArgumentSet SELF = null;

    private String url;
    private String description = null;


    /**
     * Static constructor for {@link MattermostArgumentSet} builder.
     *
     * @return empty {@link Builder}
     */
    public static MattermostArgumentSet.Builder builder() {
        return new Builder();
    }

    /**
     * Only for {@link #EMPTY_SET}.
     */
    private MattermostArgumentSet() {
    }

    /**
     * Get stored URL.
     *
     * @return non-null string with URL to shortened.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Provides stored description.
     *
     * @return stored description or Empty String
     */
    public String getDescription() {
        return Objects.isNull(description) ? "" : description;
    }

    private static synchronized MattermostArgumentSet emptySet() {
        if (Objects.isNull(SELF)) {
            SELF = new MattermostArgumentSet();
        }
        return SELF;
    }

    private static MattermostArgumentSet brokenSet() {
        MattermostArgumentSet brokenSet = new MattermostArgumentSet();
        brokenSet.url = "";
        return brokenSet;
    }

    /**
     * Creates builder and initialize it with url.
     *
     * @param url string with url
     * @return {@link Builder}
     */
    public static Builder builderWithUrl(final String url) {
        return new Builder(url);
    }

    /**
     * Builder class.
     */
    public static class Builder {
        private String urlString;
        private String descriptionString;

        Builder() {
        }

        Builder(final String url) {
            this.urlString = url;
        }

        /**
         * Builds empty set.
         *
         * @return {@link #EMPTY_SET}
         */
        @SuppressWarnings("SameReturnValue") //to improve readability
        public MattermostArgumentSet buildEmpty() {
            return EMPTY_SET;
        }

        /**
         * Adds description.
         *
         * @param description string with description
         * @return {@link Builder}
         */
        public Builder andDescription(final String description) {
            this.descriptionString = description;
            return this;
        }

        /**
         * Triggers build.
         *
         * @return built {@link MattermostArgumentSet}
         */
        public MattermostArgumentSet build() {
            if (UrlExtraValidator.isUrlValid(urlString).equals(VALID)) {
                MattermostArgumentSet newArgumentSet = new MattermostArgumentSet();
                newArgumentSet.url = urlString;
                if (StringUtils.isNotBlank(descriptionString)) {
                    newArgumentSet.description = descriptionString;
                }
                return newArgumentSet;
            } else {
                return BROKEN_SET;
            }
        }
    }
}

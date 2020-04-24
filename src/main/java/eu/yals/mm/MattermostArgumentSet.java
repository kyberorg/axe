package eu.yals.mm;

import eu.yals.utils.UrlExtraValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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

    public static Builder builderWithUrl(final String url) {
        return new Builder(url);
    }

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
        public MattermostArgumentSet buildEmpty() {
            return EMPTY_SET;
        }

        public Builder andDescription(final String description) {
            this.descriptionString = description;
            return this;
        }

        public MattermostArgumentSet build() {
            if (UrlExtraValidator.isUrl(urlString)) {
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

package eu.yals.telegram;


import eu.yals.utils.UrlExtraValidator;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static eu.yals.utils.UrlExtraValidator.VALID;

/**
 * Combinations or params present at message in {@link TelegramObject}. Similar to Unix command line args.
 *
 * @since 2.4
 */
public final class TelegramArguments {
    public static final TelegramArguments EMPTY_ARGS = TelegramArguments.emptyArgs();
    public static final TelegramArguments BROKEN_ARGS = TelegramArguments.brokenArgs();

    private static TelegramArguments SELF = null;

    @Getter
    private String url;
    private String description = null;

    /**
     * Only for {@link #EMPTY_ARGS} and {@link #BROKEN_ARGS}.
     */
    private TelegramArguments() {
    }

    /**
     * Gets description.
     *
     * @return description if present, empty string is not
     */
    public String getDescription() {
        return Objects.isNull(description) ? "" : description;
    }

    private static TelegramArguments emptyArgs() {
        if (Objects.isNull(SELF)) {
            SELF = new TelegramArguments();
        }
        return SELF;
    }

    private static TelegramArguments brokenArgs() {
        TelegramArguments brokenArgs = new TelegramArguments();
        brokenArgs.url = "";
        return brokenArgs;
    }

    /**
     * Build {@link TelegramArguments} from given URL.
     *
     * @param url string with URL
     * @return {@link Builder}
     */
    public static Builder builderWithUrl(final String url) {
        return new Builder(url);
    }

    /**
     * Empty builder.
     *
     * @return {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link TelegramArguments}.
     */
    public static class Builder {
        private String urlString;
        private String descriptionString;

        /**
         * Empty builder.
         */
        public Builder() {
        }

        /**
         * Builder with URL.
         *
         * @param url string with URL
         */
        Builder(final String url) {
            this.urlString = url;
        }

        /**
         * Builder without args.
         *
         * @return {@link #EMPTY_ARGS}
         */
        public TelegramArguments buildEmpty() {
            return EMPTY_ARGS;
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
         * @return {@link TelegramArguments}
         */
        public TelegramArguments build() {
            if (UrlExtraValidator.isUrlValid(urlString).equals(VALID)) {
                TelegramArguments newArguments = new TelegramArguments();
                newArguments.url = urlString;
                if (StringUtils.isNotBlank(descriptionString)) {
                    newArguments.description = descriptionString;
                }
                return newArguments;
            } else {
                return BROKEN_ARGS;
            }
        }
    }

    @Override
    public String toString() {
        return TelegramArguments.class.getSimpleName() + "{"
                + "url=" + url + ", "
                + "description=" + description
                + "}";
    }
}

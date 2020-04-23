package eu.yals.telegram;


import eu.yals.utils.UrlExtraValidator;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Combinations or params present at message in {@link TelegramObject}. Similar to Unix command line args.
 *
 * @since 2.4
 */
public final class TelegramArguments {
    static final TelegramArguments EMPTY_ARGS = TelegramArguments.emptyArgs();
    static final TelegramArguments BROKEN_ARGS = TelegramArguments.brokenArgs();

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

    static Builder builderWithUrl(final String url) {
        return new Builder(url);
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private String urlString;
        private String descriptionString;

        Builder() {
        }

        Builder(final String url) {
            this.urlString = url;
        }

        TelegramArguments buildEmpty() {
            return EMPTY_ARGS;
        }

        Builder andDescription(final String description) {
            this.descriptionString = description;
            return this;
        }

        public TelegramArguments build() {
            if (UrlExtraValidator.isUrl(urlString)) {
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

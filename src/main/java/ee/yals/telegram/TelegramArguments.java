package ee.yals.telegram;


import ee.yals.utils.UrlExtraValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static ee.yals.constants.App.NEW_LINE;

/**
 * Combinations or params present at message in {@link TelegramObject}. Similar to Unix command line args.
 *
 * @since 2.4
 */
public class TelegramArguments {
    static final TelegramArguments EMPTY_ARGS = TelegramArguments.emptyArgs();

    static final TelegramArguments BROKEN_ARGS = TelegramArguments.brokenArgs();

    private static TelegramArguments SELF = null;

    private String url;
    private String description = null;

    /**
     * Only for {@link #EMPTY_ARGS} and {@link #BROKEN_ARGS}
     */
    private TelegramArguments() {
    }

    public String getUrl() {
        return url;
    }

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

    static Builder builderWithUrl(String url) {
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

        Builder(String url) {
            this.urlString = url;
        }

        TelegramArguments buildEmpty() {
            return EMPTY_ARGS;
        }

        Builder andDescription(String description) {
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
        return TelegramArguments.class.getSimpleName() + "[" + NEW_LINE +
                "url=" + url + NEW_LINE +
                "description=" + description + NEW_LINE +
                "]";
    }
}

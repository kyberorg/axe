package pm.axe.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.annotations.Since;
import org.apache.commons.lang3.StringUtils;
import pm.axe.Axe;
import pm.axe.telegram.TelegramCommand;
import pm.axe.utils.AppUtils;


/**
 * JSON given back to user, when User successfully registered.
 */
@Since(2.0)
public class PostUserResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("telegram_confirmation_string")
    private String telegramConfirmationString;

    /**
     * Creates {@link PostUserResponse.Builder}.
     *
     * @return {@link PostUserResponse.Builder} to {@link Builder#build()} {@link PostUserResponse} from.
     */
    public static PostUserResponse.Builder create() {
        return new Builder();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /**
     * Builds {@link PostUserResponse}.
     */
    public static class Builder {
        private String email;
        private String telegramToken;

        /**
         * Adds email.
         *
         * @param email string with email, confirmation letter sent to.
         * @return {@link Builder} to continue building {@link PostUserResponse}.
         */
        public Builder addEmail(final String email) {
            this.email = email;
            return this;
        }

        /**
         * Adds Telegram Confirmation Token String.
         *
         * @param telegramToken string with telegram token, which links Axe Account with Telegram account.
         */
        public void addTelegramToken(final String telegramToken) {
            this.telegramToken = telegramToken;
        }

        /**
         * Builds {@link PostUserResponse}. Creates message and Telegram confirmation String, based on input.
         *
         * @return new {@link PostUserResponse} with {@link #message} and {@link #telegramConfirmationString} filled in.
         */
        public PostUserResponse build() {
            boolean accountHasNoConfirmationMethods = true;
            PostUserResponse response = new PostUserResponse();
            StringBuilder sb = new StringBuilder("Account is registered. ");

            if (StringUtils.isNotBlank(email)) {
                //got email
                sb.append(String.format("Confirmation e-mail sent to %s. ", email));
                accountHasNoConfirmationMethods = false;
            }
            if (StringUtils.isNotBlank(telegramToken)) {
                final String telegramHelloCmd = TelegramCommand.HELLO.getCommandText();
                response.telegramConfirmationString = String.format("%s %s", telegramHelloCmd, telegramToken);
                sb.append("By the way, you can also ");
                if (accountHasNoConfirmationMethods) {
                    sb.append("confirm your account in Telegram ");
                } else {
                    sb.append("link your account in Telegram ");
                }
                sb.append("by sending following string '")
                        .append(telegramHelloCmd).append(" ").append(telegramToken).append("' to ")
                        .append(Axe.C.AT).append(AppUtils.getTelegramBotName());
            }
            response.message = sb.toString();
            return response;
        }
    }
}

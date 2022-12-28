package pm.axe.telegram.handlers;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import pm.axe.Axe;
import pm.axe.db.models.User;
import pm.axe.telegram.TelegramUserMapping;
import pm.axe.utils.AppUtils;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class MyAxeUserHandler implements TelegramCommandHandler {
    private final TelegramUserMapping userMapping;
    private final AppUtils appUtils;

    @Override
    public String handle(final Update update) {
        //get message from update
        final Message message = update.getMessage();

        final String tgUser = message.getFrom().getUserName();
        Optional<User> axeUser = userMapping.getAxeUser(tgUser);
        return axeUser.map(user -> EmojiParser.parseToUnicode(String.format("%s Your linked %s User is `%s`",
                Axe.Emoji.USER, StringUtils.capitalize(appUtils.getServerDomain().toLowerCase()),
                user.getUsername())))
                .orElseGet(() -> EmojiParser.parseToUnicode(
                        Axe.Emoji.NO_USER + " No user linked yet, you can generate find your token at profile page"));
    }
}

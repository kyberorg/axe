package ee.yals.telegram;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Objects;

/**
 * Telegram Bot
 *
 * @since 2.4
 */
public class TelegramBot extends TelegramLongPollingBot {
    private static final String DUMMY_TOKEN = "dummy:Token";

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Update " + update.getMessage().getText());

        SendMessage sendMessage = new SendMessage().setChatId(update.getMessage().getChatId());
        sendMessage.setText("Terve " + update.getMessage().getFrom().getUserName() + " sinu sonum oli: " +
                update.getMessage().getText());

        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "Yals Bot";
    }

    @Override
    public String getBotToken() {
        String token = System.getenv("TELEGRAM_TOKEN");
        return Objects.isNull(token) ? DUMMY_TOKEN : token;
    }
}

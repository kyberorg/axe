package ee.yals.telegram;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

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
        sendMessage.setText("Terve");

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
        return System.getProperty("telegram.bot.token", DUMMY_TOKEN);
    }
}

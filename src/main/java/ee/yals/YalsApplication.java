package ee.yals;

import ee.yals.telegram.TelegramBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

/**
 * Main (Start point)
 */
@SpringBootApplication
public class YalsApplication {

	public static void main(String[] args) {
		SpringApplication.run(YalsApplication.class, args);
		telegram();
	}

	private static void telegram() {
        ApiContextInitializer.init();
		TelegramBotsApi botsApi = new TelegramBotsApi();
		TelegramBot bot = new TelegramBot();
		try {
			botsApi.registerBot(bot);
		} catch (TelegramApiRequestException e) {
			e.printStackTrace();
		}
	}
}

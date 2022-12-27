package pm.axe.telegram.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartHandler implements TelegramCommandHandler{
    @Override
    public String handle(Update update) {
        //FIXME impl
        return null;
    }
}

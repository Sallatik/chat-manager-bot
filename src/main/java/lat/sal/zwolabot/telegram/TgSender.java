package lat.sal.zwolabot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;

@Component
public class TgSender {

    private Logger logger = Logger.getLogger(TgSender.class.getName());

    private TelegramBot telegramBot;

    public <T extends BaseRequest, R extends BaseResponse> R executeOrLog(BaseRequest<T, R> request) {

        R response = telegramBot.execute(request);

        if (!response.isOk())
            logger.warning("Telegram error: " + response.description());

        return response;
    }

    @Autowired
    public TgSender(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }
}

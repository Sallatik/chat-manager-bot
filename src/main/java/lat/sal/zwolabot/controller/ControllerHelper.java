package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lat.sal.zwolabot.telegram.TgSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ControllerHelper {

    private TgSender tgSender;

    public String getArgument(String text, int offset) {
        return text.trim().substring(offset).trim();
    }

    public void reply(String text, Message message) {

        tgSender.executeOrLog(
                new SendMessage(message.chat().id(), text)
                        .replyToMessageId(message.messageId())
                        .parseMode(ParseMode.Markdown)
                        .disableWebPagePreview(true)
        );
    }

    @Autowired
    public ControllerHelper(TgSender tgSender) {
        this.tgSender = tgSender;
    }
}

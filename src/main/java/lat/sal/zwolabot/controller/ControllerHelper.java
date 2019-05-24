package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import lat.sal.zwolabot.ZwolabotException;
import lat.sal.zwolabot.entity.User;
import lat.sal.zwolabot.service.UserService;
import lat.sal.zwolabot.telegram.TgSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ControllerHelper {

    private TgSender tgSender;
    private UserService userService;

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

    public void stickerReply(String sticker, Message message) {

        tgSender.executeOrLog(
                new SendSticker(message.chat().id(), sticker)
                .replyToMessageId(message.messageId())
        );
    }

    public User getTargetUser(Message message) {

        if (message.replyToMessage() != null) {
            return new User(message.replyToMessage().from());

        } else {

            String [] parts = message.text().trim().split("\\s");

            if (parts.length < 2)
                throw new ZwolabotException("каво");

            String username = parts[1];

            if (username.charAt(0) == '@')
                username = username.substring(1);

            return userService.getUserByUsername(username);
        }
    }

    @Autowired
    public ControllerHelper(TgSender tgSender, UserService userService) {
        this.tgSender = tgSender;
        this.userService = userService;
    }
}

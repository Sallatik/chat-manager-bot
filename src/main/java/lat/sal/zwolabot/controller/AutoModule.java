package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Message;
import lat.sal.zwolabot.entity.User;
import lat.sal.zwolabot.service.ChatService;
import lat.sal.zwolabot.service.FilterService;
import lat.sal.zwolabot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sallat.jelebot.annotation.listeners.MessageListener;

import java.util.Date;

@Component
public class AutoModule {

    private UserService userService;
    private ChatService chatService;
    private FilterService filterService;

    @MessageListener(filter = "private | supergroup")
    public void updateUser(Message message) {
        if (message.from().isBot())
            return;
        userService.updateUser(new User(message.from()));
    }

    @MessageListener(filter = "supergroup & not join")
    public void groupMessage(Message message) {
        if (message.from().isBot())
            return;

        chatService.onMessage(
                message.chat().id(),
                message.from().id(),
                new Date(message.date())
        );
    }

    @MessageListener(filter = "join")
    public void newUsers(Message message) {

        for (com.pengrad.telegrambot.model.User user : message.newChatMembers()) {
            if (!user.isBot())
                chatService.onMessage(
                        message.chat().id(),
                        user.id(),
                        new Date(message.date())
                );
        }
    }

    @MessageListener(filter = "supergroup & text")
    public void censorText(Message message) {
        if (message.from().isBot())
            return;

        filterService.onTextMessage(
                message.text(),
                message.chat().id(),
                message.from().id(),
                message.messageId()
        );
    }

    @MessageListener(filter = "supergroup & caption")
    public void censorCaption(Message message) {
        if (message.from().isBot())
            return;

        filterService.onTextMessage(
                message.caption(),
                message.chat().id(),
                message.from().id(),
                message.messageId()
        );
    }

    @MessageListener(filter = "supergroup & sticker")
    public void censorSticker(Message message) {

        filterService.onStickerMessage(
                message.sticker().setName(),
                message.sticker().fileId(),
                message.chat().id(),
                message.from().id(),
                message.messageId()
        );
    }

    @Autowired
    public AutoModule(UserService userService, ChatService chatService, FilterService filterService) {
        this.userService = userService;
        this.chatService = chatService;
        this.filterService = filterService;
    }
}

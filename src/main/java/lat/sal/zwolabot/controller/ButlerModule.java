package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Message;
import lat.sal.zwolabot.controller.annotation.Admin;
import lat.sal.zwolabot.controller.annotation.Moderator;
import lat.sal.zwolabot.controller.annotation.Respond;
import lat.sal.zwolabot.entity.User;
import lat.sal.zwolabot.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sallat.jelebot.annotation.listeners.MessageListener;

@Component
public class ButlerModule {

    private ControllerHelper helper;
    private ChatService chatService;
    private Notifier notifier;

    @Moderator
    @Respond
    @MessageListener(filter = "/ban & supergroup")
    public void ban(Message message) {

        User user = helper.getTargetUser(message);
        String note = helper.getComment(message);
        chatService.ban(message.chat().id(), user.getId(), note);
        String response = helper.userLink(new User(message.from())) + " забанил пользователя " + helper.userLink(user)
                + (note.equals("") ? "" : " по причине: _" + note + "_.") + "\nПомянем!";
        helper.replyDelete(response, message);
        notifier.notifyBan(user.getId(), message.chat(), note, message.from());
    }

    @Moderator
    @Respond
    @MessageListener(filter = "/unban & supergroup")
    public void unban(Message message) {

        User user = helper.getTargetUser(message);
        chatService.unban(message.chat().id(), user.getId());
        String response = helper.userLink(new User(message.from())) + " разбанил пользователя " + helper.userLink(user);
        helper.replyDelete(response, message);
        notifier.notifyUnban(user.getId(), message.chat(), message.from());
    }

    @Admin
    @Respond
    @MessageListener(filter = "/promote & supergroup")
    public void promote(Message message) {

        User user = helper.getTargetUser(message);
        chatService.setModerator(message.chat().id(), user.getId(), true);
        String response = helper.userLink(user) + " теперь модератор.";
        helper.replyDelete(response, message);
        notifier.notifyModerator(user.getId(), message.chat(), message.from());
    }

    @Admin
    @Respond
    @MessageListener(filter = "/demote & supergroup")
    public void demote(Message message) {

        User user = helper.getTargetUser(message);
        chatService.setModerator(message.chat().id(), user.getId(), false);
        String response = helper.userLink(user) + " больше не модератор.";
        helper.replyDelete(response, message);
        notifier.notifyNoModerator(user.getId(), message.chat(), message.from());
    }

    @Moderator
    @Respond
    @MessageListener(filter = "/warn & supergroup")
    public void warn(Message message) {

        User user = helper.getTargetUser(message);
        int warns = chatService.warn(message.chat().id(), user.getId());
        String response = helper.userLink(user) + " предупреждён: " + warns + "/3\n" +
                (warns == 3 ? "И забанен, собрав максимальное количество предупреждений. Помянем!" : "");
        helper.replyDelete(response, message);
    }

    @Moderator
    @Respond
    @MessageListener(filter = "/clear & supergroup")
    public void clearWarns(Message message) {

        User user = helper.getTargetUser(message);
        chatService.clearWarns(message.chat().id(), user.getId());
        String response = "Рабу божию " + helper.userLink(user) + " были отпущены все грехи. Аминь!";
        helper.replyDelete(response, message);
    }

    @Moderator
    @Respond
    @MessageListener(filter = "/user & supergroup")
    public void getUserInfo(Message message) {

        User user = helper.getTargetUser(message);
        String response = chatService.getChatUser(message.chat().id(), user.getId()).toString();
        helper.replyDelete(response, message);
    }

    @Autowired
    public ButlerModule(ControllerHelper helper, ChatService chatService, Notifier notifier) {
        this.helper = helper;
        this.chatService = chatService;
        this.notifier = notifier;
    }
}

package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Message;
import lat.sal.zwolabot.ZwolabotException;
import lat.sal.zwolabot.entity.User;
import lat.sal.zwolabot.service.ChatService;
import lat.sal.zwolabot.service.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sallat.jelebot.annotation.listeners.MessageListener;

@Component
public class ButlerModule {

    private ErrorService errorService;
    private ControllerHelper helper;
    private ChatService chatService;

    @MessageListener(filter = "/ban & supergroup")
    public void ban(Message message) {
        helper.respond(message, () -> {

            errorService.reqireAdminOrModerator(message.chat().id(), message.from().id());
            User user = helper.getTargetUser(message);
            String note = ""; // todo: fix
            chatService.ban(message.chat().id(), user.getId(), note);
            return message.from().firstName() + " забанил(а) пользователя " + user.getFirstName()
                    + (note.equals("") ? "" : " по причине: _" + note + "_.") + "\nПомянем!";
        });
    }

    @MessageListener(filter = "/unban & supergroup")
    public void unban(Message message) {
        helper.respond(message, () -> {

            errorService.reqireAdminOrModerator(message.chat().id(), message.from().id());
            User user = helper.getTargetUser(message);
            chatService.unban(message.chat().id(), user.getId());
            return message.from().firstName() + " разбанил(а) пользователя " + user.getFirstName();
        });
    }

    @MessageListener(filter = "/promote & supergroup")
    public void promote(Message message) {
        helper.respond(message, () -> {

            errorService.requireAdmin(message.from().id());
            User user = helper.getTargetUser(message);
            chatService.setModerator(message.chat().id(), user.getId(), true);
            return user.getFirstName() + " теперь модератор.";
        });
    }


    @MessageListener(filter = "/demote & supergroup")
    public void demote(Message message) {
        helper.respond(message, () -> {

            errorService.requireAdmin(message.from().id());
            User user = helper.getTargetUser(message);
            chatService.setModerator(message.chat().id(), user.getId(), false);
            return user.getFirstName() + " больше не модератор.";
        });
    }

    @MessageListener(filter = "/warn & supergroup")
    public void warn(Message message) {
        helper.respond(message, () -> {

            errorService.reqireAdminOrModerator(message.chat().id(), message.from().id());
            User user = helper.getTargetUser(message);
            int warns = chatService.warn(message.chat().id(), user.getId());
            return user.getFirstName() + " предупреждён: " + warns + "/3\n" +
                    (warns == 3 ? "И забанен, собрав максимальное количество предупреждений. Помянем!" : "");
        });
    }

    @MessageListener(filter = "/clear & supergroup")
    public void clearWarns(Message message) {
        helper.respond(message, () -> {

            errorService.reqireAdminOrModerator(message.chat().id(), message.from().id());
            User user = helper.getTargetUser(message);
            chatService.clearWarns(message.chat().id(), user.getId());
            return "Рабу божию " + user.getFirstName() + " были отпущены все грехи. Аминь!";
        });
    }

    @Autowired
    public ButlerModule(ErrorService errorService, ControllerHelper helper, ChatService chatService) {
        this.errorService = errorService;
        this.helper = helper;
        this.chatService = chatService;
    }
}

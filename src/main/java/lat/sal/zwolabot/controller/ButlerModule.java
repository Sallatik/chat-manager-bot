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

        try {

            errorService.reqireAdminOrModerator(message.chat().id(), message.from().id());

            User user = helper.getTargetUser(message);
            String note = ""; // todo: fix

            chatService.ban(message.chat().id(), user.getId(), note);
            helper.reply(message.from().firstName() + " забанил(а) пользователя " + user.getFirstName()
                    + (note.equals("") ? "" : " по причине: _" + note + "_.") + "\nПомянем!", message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/unban & supergroup")
    public void unban(Message message) {

        try {

            errorService.reqireAdminOrModerator(message.chat().id(), message.from().id());
            User user = helper.getTargetUser(message);
            chatService.unban(message.chat().id(), user.getId());
            helper.reply(message.from().firstName() + " разбанил(а) пользователя " + user.getFirstName(), message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/promote & supergroup")
    public void promote(Message message) {

        try {

            errorService.requireAdmin(message.from().id());
            User user = helper.getTargetUser(message);
            chatService.setModerator(message.chat().id(), user.getId(), true);
            helper.reply(user.getFirstName() + " теперь модератор.", message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }


    @MessageListener(filter = "/demote & supergroup")
    public void demote(Message message) {

        try {

            errorService.requireAdmin(message.from().id());
            User user = helper.getTargetUser(message);
            chatService.setModerator(message.chat().id(), user.getId(), false);
            helper.reply(user.getFirstName() + " больше не модератор.", message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/warn & supergroup")
    public void warn(Message message) {

        try {

            errorService.reqireAdminOrModerator(message.chat().id(), message.from().id());
            User user = helper.getTargetUser(message);
            int warns = chatService.warn(message.chat().id(), user.getId());
            helper.reply(user.getFirstName() + " предупреждён: " + warns + "/3\n" +
                    (warns == 3 ? "И забанен, собрав максимальное количество предупреждений. Помянем!" : ""), message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/clear & supergroup")
    public void clearWarns(Message message) {

        try {

            errorService.reqireAdminOrModerator(message.chat().id(), message.from().id());
            User user = helper.getTargetUser(message);
            chatService.clearWarns(message.chat().id(), user.getId());
            helper.reply("Рабу божию " + user.getFirstName() + " были отпущены все грехи. Аминь!", message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @Autowired
    public ButlerModule(ErrorService errorService, ControllerHelper helper, ChatService chatService) {
        this.errorService = errorService;
        this.helper = helper;
        this.chatService = chatService;
    }
}

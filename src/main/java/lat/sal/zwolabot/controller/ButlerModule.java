package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Message;
import lat.sal.zwolabot.ZwolabotException;
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

    @MessageListener(filter = "/ban & supergroup & reply")
    public void ban(Message message) {

        try {

            errorService.reqireAdminOrModerator(message.chat().id(), message.from().id());
            String note = helper.getArgument(message.text(), "/ban".length());

            chatService.ban(message.chat().id(), message.replyToMessage().from().id(), note);
            helper.reply(message.from().firstName() + " забанил(а) пользователя " + message.replyToMessage().from().firstName()
                    + (note.equals("") ? "" : " по причине: _" + note + "_.") + "\nПомянем!", message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/unban & reply & supergroup")
    public void unban(Message message) {

        try {

            errorService.reqireAdminOrModerator(message.chat().id(), message.from().id());
            chatService.unban(message.chat().id(), message.replyToMessage().from().id());
            helper.reply(message.from().firstName() + " разбанил(а) пользователя " + message.replyToMessage().from().firstName(), message);
        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/promote & supergroup & reply")
    public void promote(Message message) {

        try {

            errorService.requireAdmin(message.from().id());
            chatService.setModerator(message.chat().id(), message.replyToMessage().from().id(), true);
            helper.reply(message.replyToMessage().from().firstName() + " теперь модератор.", message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }


    @MessageListener(filter = "/demote & supergroup & reply")
    public void demote(Message message) {

        try {

            errorService.requireAdmin(message.from().id());
            chatService.setModerator(message.chat().id(), message.replyToMessage().from().id(), false);
            helper.reply(message.replyToMessage().from().firstName() + " больше не модератор.", message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/warn & supergroup & reply")
    public void warn(Message message) {

        try {

            errorService.reqireAdminOrModerator(message.chat().id(), message.from().id());
            int warns = chatService.warn(message.chat().id(), message.replyToMessage().from().id());
            helper.reply(message.replyToMessage().from().firstName() + " предупреждён: " + warns + "/3\n" +
                    (warns == 3 ? "И забанен, собрав максимальное количество предупреждений. Помянем!" : ""), message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/clear & supergroup & reply")
    public void clearWarns(Message message) {

        try {

            errorService.reqireAdminOrModerator(message.chat().id(), message.from().id());
            chatService.clearWarns(message.chat().id(), message.replyToMessage().from().id());
            helper.reply("Рабу божию " + message.replyToMessage().from().firstName() + " были отпущены все грехи. Аминь!", message);

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

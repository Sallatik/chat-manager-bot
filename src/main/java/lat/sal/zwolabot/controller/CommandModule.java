package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMediaGroup;
import com.pengrad.telegrambot.request.SendMessage;
import lat.sal.zwolabot.ZwolabotException;
import lat.sal.zwolabot.entity.Chat;
import lat.sal.zwolabot.entity.User;
import lat.sal.zwolabot.service.ChatService;
import lat.sal.zwolabot.service.LevelAndChats;
import lat.sal.zwolabot.service.UserService;
import lat.sal.zwolabot.telegram.TgSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sallat.jelebot.annotation.listeners.MessageListener;

import java.lang.reflect.Member;

@Component
public class CommandModule {

    private UserService userService;
    private TgSender tgSender;
    private ChatService chatService;

    @MessageListener(filter = "/start & private")
    public void register(Message message) {

        User user = new User(message.from());

        try {
            userService.addUser(user);
            reply("Добро пожаловать!", message);
        } catch (ZwolabotException e) {
            reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/info & private")
    public void getLevel(Message message) {

        try {

            LevelAndChats levelAndChats = userService.getLevelAndAvailableChats(message.from().id());
            StringBuilder result = new StringBuilder("Ваш уровень доступа: *" + levelAndChats.getLevel().getName() + "*\n" + levelAndChats.getLevel().getDescription() + "\n\nДоступные чаты:");

            for (Chat chat : levelAndChats.getChats()) {

                result.append("\n[" + chat.getTitle() + "](" + chat.getInviteLink() + "): " + chat.getDescription());
            }

            SendMessage sendMessage = new SendMessage(message.chat().id(), result.toString())
                    .replyToMessageId(message.messageId())
                    .disableWebPagePreview(true)
                    .parseMode(ParseMode.Markdown);

            tgSender.executeOrLog(sendMessage);

        } catch (ZwolabotException e) {
            reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/addchat & text & supergroup")
    public void addChat(Message message) {

        if (!userService.isAdmin(message.from().id())) {
            reply("Вы не админ", message);
            return;
        }

        String level = getArgument(message.text(), "/addchat".length());

        try {
            chatService.addChat(message.chat().id(), level);
            reply("Готово", message);
        } catch (ZwolabotException e) {
            reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/setlevel & text & reply")
    public void setUserLevel(Message message) {

        if (!userService.isAdmin(message.from().id())) {
            reply("Вы не админ", message);
            return;
        }

        String level = getArgument(message.text(), "/setlevel".length());

        try {
            userService.setUserAccessLevel(message.replyToMessage().from().id(), level);
            reply("Готово", message);
        } catch (ZwolabotException e) {
            reply(e.getMessage(), message);
        }
    }

    // butler

    @MessageListener(filter = "/ban & supergroup & reply")
    public void ban(Message message) {

        if (!(userService.isAdmin(message.from().id()) || chatService.isModerator(message.chat().id(), message.from().id()))) {
            reply("Вы не админ", message);
            return;
        }

        String note = getArgument(message.text(), "/ban".length());

        try {
            chatService.ban(message.chat().id(), message.replyToMessage().from().id(), note);
            reply(message.from().firstName() + " забанил(а) пользователя " + message.replyToMessage().from().firstName()
                    + (note.equals("") ? "" : " по причине: _" + note + "_.") + "\nПомянем!", message);

        } catch (ZwolabotException e) {
            reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/unban & reply & supergroup")
    public void unban(Message message) {

        if (!(userService.isAdmin(message.from().id()) || chatService.isModerator(message.chat().id(), message.from().id()))) {
            reply("Вы не админ", message);
            return;
        }

        try {

            chatService.unban(message.chat().id(), message.replyToMessage().from().id());
            reply(message.from().firstName() + " разбанил(а) пользователя " + message.replyToMessage().from().firstName(), message);
        } catch (ZwolabotException e) {
            reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/promote & supergroup & reply")
    public void promote(Message message) {

        if (!userService.isAdmin(message.from().id())) {
            reply("Вы не админ", message);
            return;
        }

        try {
            chatService.setModerator(message.chat().id(), message.replyToMessage().from().id(), true);
            reply(message.replyToMessage().from().firstName() + " теперь модератор.", message);
        } catch (ZwolabotException e) {
            reply(e.getMessage(), message);
        }
    }


    @MessageListener(filter = "/demote & supergroup & reply")
    public void demote(Message message) {

        if (!userService.isAdmin(message.from().id())) {
            reply("Вы не админ", message);
            return;
        }

        try {
            chatService.setModerator(message.chat().id(), message.replyToMessage().from().id(), false);
            reply(message.replyToMessage().from().firstName() + " больше не модератор.", message);
        } catch (ZwolabotException e) {
            reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/warn & supergroup & reply")
    public void warn(Message message) {

        if (!userService.isAdmin(message.from().id())) {
            reply("Вы не админ", message);
            return;
        }

        try {

            int warns = chatService.warn(message.chat().id(), message.replyToMessage().from().id());
            reply(message.replyToMessage().from().firstName() + " предупреждён: " + warns + "/3\n" +
                    (warns == 3 ? "И забанен, собрав максимальное количество предупреждений. Помянем!" : ""), message);

        } catch (ZwolabotException e) {
            reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/clear & supergroup & reply")
    public void clearWarns(Message message) {

        if (!userService.isAdmin(message.from().id())) {
            reply("Вы не админ", message);
            return;
        }

        try {

            chatService.clearWarns(message.chat().id(), message.replyToMessage().from().id());
            reply("Рабу божию " + message.replyToMessage().from().firstName() + " были отпущены все грехи. Аминь!", message);

        } catch (ZwolabotException e) {
            reply(e.getMessage(), message);
        }
    }

    private String getArgument(String text, int offset) {
        return text.trim().substring(offset).trim();
    }

    private void reply(String text, Message message) {

        tgSender.executeOrLog(
                new SendMessage(message.chat().id(), text)
                        .replyToMessageId(message.messageId())
                        .parseMode(ParseMode.Markdown)
        );
    }

    @Autowired
    public CommandModule(UserService userService, TgSender tgSender, ChatService chatService) {
        this.userService = userService;
        this.tgSender = tgSender;
        this.chatService = chatService;
    }
}

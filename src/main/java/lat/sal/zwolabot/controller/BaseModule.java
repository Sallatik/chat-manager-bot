package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Message;
import lat.sal.zwolabot.ZwolabotException;
import lat.sal.zwolabot.entity.Chat;
import lat.sal.zwolabot.entity.User;
import lat.sal.zwolabot.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sallat.jelebot.annotation.listeners.MessageListener;

import java.util.Date;

@Component
public class BaseModule {

    private UserService userService;
    private ChatService chatService;
    private ErrorService errorService;
    private SettingsService settingsService;
    private ControllerHelper helper;

    @MessageListener(filter = "/start & private")
    public void register(Message message) {
        helper.respond(message, () -> {

            User user = new User(message.from());
            userService.addUser(user);
            return "Добро пожаловать! Чтобы получить информацию о вашем уровне доступа, " +
                    "а так же список доступных чатов, используйте команду /info";

        });
    }

    @MessageListener(filter = "/info & private")
    public void getLevel(Message message) {
        helper.respond(message, () -> {

            LevelAndChats levelAndChats = userService.getLevelAndAvailableChats(message.from().id());
            StringBuilder result = new StringBuilder("Ваш уровень доступа: *" +
                    levelAndChats.getLevel().getName() + "*\n" +
                    levelAndChats.getLevel().getDescription() +
                    "\n\nДоступные чаты:");

            for (Chat chat : levelAndChats.getChats())
                result.append("\n[" + chat.getTitle() + "]" +
                        "(" + chat.getInviteLink() + "): " + (chat.getDescription() == null ? "" : chat.getDescription()));

            return result.toString();
        });
    }

    @MessageListener(filter = "/addchat & text & supergroup")
    public void addChat(Message message) {
        helper.respond(message, () -> {

            errorService.requireAdmin(message.from().id());
            String level = helper.getArgument(message.text(), "/addchat".length());

            chatService.addChat(message.chat().id(), level);
            return "Чат успешно добавлен в систему с уровнем доступа '" + level + "'";
        });
    }

    @MessageListener(filter = "/update & supergroup")
    public void updateChat(Message message) {
        helper.respond(message, () -> {

            errorService.requireAdmin(message.from().id());
            chatService.updateChat(message.chat().id());
            return "Информация о чате успешно обновлена";
        });
    }

    @MessageListener(filter = "/setlevel & text & reply")
    public void setUserLevel(Message message) {
        helper.respond(message, () -> {

            errorService.requireAdmin(message.from().id());
            String level = helper.getArgument(message.text(), "/setlevel".length());
            User user = new User(message.replyToMessage().from());
            userService.setUserAccessLevel(user.getId(), level);
            return "Пользователю " + helper.userLink(user) + " присвоен уровень доступа '" + level + "'";
        });
    }

    @MessageListener(filter = "/admin")
    public void setAdmin(Message message) {
        helper.respond(message, () -> {

            errorService.requireRoot(message.from().id());
            User user = helper.getTargetUser(message);
            userService.setAdmin(user.getId(), true);
            return "Пользователь " + helper.userLink(user) + " наделён полномочиями админа";
        });
    }

    @MessageListener(filter = "/noadmin")
    public void setNoAdmin(Message message) {
        helper.respond(message, () -> {

            errorService.requireRoot(message.from().id());
            User user = helper.getTargetUser(message);
            userService.setAdmin(user.getId(), false);
            return "Пользователь " + helper.userLink(user) + " освобождён от полномочий админа";
        });
    }

    @MessageListener(filter = "/gban")
    public void gban(Message message) {
        helper.respond(message, () -> {

            errorService.requireAdmin(message.from().id());
            User user = helper.getTargetUser(message);
            userService.setUserAccessLevel(user.getId(), "ban");
            return helper.userLink(user) + " заблокирован во всех чатах";
        });
    }

    @MessageListener(filter = "/close")
    public void close(Message message) {
        helper.respond(message, () -> {

            errorService.requireAdmin(message.from().id());
            settingsService.setRegistrationOpen(false);
            return "Регистрация новых пользователей временно приостановлена";
        });
    }

    @MessageListener(filter = "/open")
    public void open(Message message) {
        helper.respond(message, () -> {

            errorService.requireAdmin(message.from().id());
            settingsService.setRegistrationOpen(true);
            return "Регистрация новых пользователей возобновлена";
        });
    }

    @MessageListener(filter = "/settings")
    public void settings(Message message) {
        helper.respond(message, () -> {

            errorService.requireAdmin(message.from().id());
            return settingsService.getSettings().toString();
        });
    }

    @MessageListener(filter = "/guser")
    public void getUserInfo(Message message) {
        helper.respond(message, () -> {

            errorService.requireAdmin(message.from().id());
            User user = helper.getTargetUser(message);
            user = userService.getUser(user.getId());
            return user.toString();
        });
    }

    @MessageListener(filter = "/chatinfo")
    public void getChatInfo(Message message) {
        helper.respond(message, () -> {

            errorService.requireAdmin(message.from().id());
            return chatService.getChat(message.chat().id()).toString();
        });
    }

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

    @Autowired
    public BaseModule(UserService userService, ChatService chatService, ErrorService errorService, SettingsService settingsService, ControllerHelper helper) {
        this.userService = userService;
        this.chatService = chatService;
        this.errorService = errorService;
        this.settingsService = settingsService;
        this.helper = helper;
    }
}

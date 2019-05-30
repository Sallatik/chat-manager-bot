package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lat.sal.zwolabot.controller.annotation.Admin;
import lat.sal.zwolabot.controller.annotation.Respond;
import lat.sal.zwolabot.controller.annotation.Root;
import lat.sal.zwolabot.entity.Chat;
import lat.sal.zwolabot.entity.User;
import lat.sal.zwolabot.service.*;
import lat.sal.zwolabot.telegram.TgSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sallat.jelebot.annotation.listeners.MessageListener;

@Component
public class BaseModule {

    private UserService userService;
    private ChatService chatService;
    private SettingsService settingsService;
    private ControllerHelper helper;
    private TgSender tgSender;

    @Respond
    @MessageListener(filter = "/start & private")
    public void register(Message message) {

        User user = new User(message.from());
        userService.addUser(user);
        String response = "Добро пожаловать! Чтобы получить информацию о вашем уровне доступа, " +
                "а так же список доступных чатов, используйте команду /info";

        SendMessage sendMessage = new SendMessage(message.chat().id(), response)
                .replyMarkup(new ReplyKeyboardMarkup(new String [] {"Доступные чаты"}).resizeKeyboard(true));

        tgSender.executeOrLog(sendMessage);
    }

    @Respond
    @MessageListener(filter = "/info & private")
    public void getLevelByCommand(Message message) {
        getLevel(message);
    }

    @Respond
    @MessageListener(filter = "private & text")
    public void getLevelByButton(Message message) {
        if (message.text().equals("Доступные чаты"))
            getLevel(message);
    }

    private void getLevel(Message message) {

        LevelAndChats levelAndChats = userService.getLevelAndAvailableChats(message.from().id());
        StringBuilder result = new StringBuilder("Ваш уровень доступа: *" +
                levelAndChats.getLevel().getName() + "*\n" +
                levelAndChats.getLevel().getDescription());

        if (levelAndChats.getChats().isEmpty())
            result.append("\n\nНет доступных чатов.");
        else
            result.append("\n\nДоступные чаты: ");

        for (Chat chat : levelAndChats.getChats())
            result.append("\n[" + chat.getTitle() + "]" +
                    "(" + chat.getInviteLink() + "): " + (chat.getDescription() == null ? "" : chat.getDescription()));

        String response = result.toString();
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/addchat & text & supergroup")
    public void addChat(Message message) {

        String level = helper.getSingleArg(message.text());

        chatService.addChat(message.chat().id(), level);
        String response = "Чат успешно добавлен в систему с уровнем доступа '" + level + "'";
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/update & supergroup")
    public void updateChat(Message message) {

        chatService.updateChat(message.chat().id());
        String response = "Информация о чате успешно обновлена";
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/setlevel & text")
    public void setUserLevel(Message message) {

        User user = helper.getTargetUser(message);
        String level = helper.getComment(message);

        userService.setUserAccessLevel(user.getId(), level);
        String response = "Пользователю " + helper.userLink(user) + " присвоен уровень доступа '" + level + "'";
        helper.reply(response, message);
    }

    @Root
    @Respond
    @MessageListener(filter = "/admin")
    public void setAdmin(Message message) {

        User user = helper.getTargetUser(message);
        userService.setAdmin(user.getId(), true);
        String response = "Пользователь " + helper.userLink(user) + " наделён полномочиями админа";
        helper.reply(response, message);

    }

    @Root
    @Respond
    @MessageListener(filter = "/noadmin")
    public void setNoAdmin(Message message) {

        User user = helper.getTargetUser(message);
        userService.setAdmin(user.getId(), false);
        String response = "Пользователь " + helper.userLink(user) + " освобождён от полномочий админа";
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/gban")
    public void gban(Message message) {

        User user = helper.getTargetUser(message);
        userService.setUserAccessLevel(user.getId(), "ban");
        String response = helper.userLink(user) + " заблокирован во всех чатах";
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/close")
    public void close(Message message) {

        settingsService.setRegistrationOpen(false);
        String response = "Регистрация новых пользователей временно приостановлена";
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/open")
    public void open(Message message) {

        settingsService.setRegistrationOpen(true);
        String response = "Регистрация новых пользователей возобновлена";
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/settings")
    public void settings(Message message) {

        String response = settingsService.getSettings().toString();
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/guser")
    public void getUserInfo(Message message) {

        User user = helper.getTargetUser(message);
        user = userService.getUser(user.getId());
        String response = user.toString();
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/chatinfo")
    public void getChatInfo(Message message) {

        String response = chatService.getChat(message.chat().id()).toString();
        helper.reply(response, message);
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
                message.date()
        );
    }

    @MessageListener(filter = "join")
    public void newUsers(Message message) {

        for (com.pengrad.telegrambot.model.User user : message.newChatMembers()) {
            if (!user.isBot())
                chatService.onMessage(
                        message.chat().id(),
                        user.id(),
                        message.date()
                );
        }
    }

    @Autowired
    public BaseModule(UserService userService, ChatService chatService, SettingsService settingsService, ControllerHelper helper, TgSender tgSender) {
        this.userService = userService;
        this.chatService = chatService;
        this.settingsService = settingsService;
        this.helper = helper;
        this.tgSender = tgSender;
    }
}

package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lat.sal.zwolabot.ZwolabotException;
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
    private Notifier notifier;

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
            result.append("\n\nДоступные чаты:\n");

        for (Chat chat : levelAndChats.getChats())
            result.append("\n— [" + chat.getTitle() + "]" +
                    "(" + chat.getInviteLink() + "): " + (chat.getDescription() == null ? "" : chat.getDescription()) + "\n");

        String response = result.toString();

        String picFileId = settingsService.getSettings().getChatListPicFileId();
        if (picFileId != null)
            helper.replyPicCaptionDelete(picFileId, response, message);
        else
            helper.replyDelete(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/addchat & text & supergroup")
    public void addChat(Message message) {

        String level = helper.getSingleArg(message.text());

        chatService.addChat(message.chat().id(), level);
        String response = "Чат успешно добавлен в систему с уровнем доступа '" + level + "'";
        helper.replyDelete(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/update & supergroup")
    public void updateChat(Message message) {

        chatService.updateChat(message.chat().id());
        String response = "Информация о чате успешно обновлена";
        helper.replyDelete(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/setlevel & text")
    public void setUserLevel(Message message) {

        User user = helper.getTargetUser(message);
        String level = helper.getComment(message);

        userService.setUserAccessLevel(user.getId(), level);
        String response = "Пользователю " + helper.userLink(user) + " присвоен уровень доступа '" + level + "'";
        helper.replyDelete(response, message);
        notifier.notifyNewLevel(user.getId(), level, message.from());
    }

    @Root
    @Respond
    @MessageListener(filter = "/admin")
    public void setAdmin(Message message) {

        User user = helper.getTargetUser(message);
        userService.setAdmin(user.getId(), true);
        String response = "Пользователь " + helper.userLink(user) + " наделён полномочиями админа";
        helper.replyDelete(response, message);
        notifier.notifyAdmin(user.getId(), message.from());

    }

    @Root
    @Respond
    @MessageListener(filter = "/noadmin")
    public void setNoAdmin(Message message) {

        User user = helper.getTargetUser(message);
        userService.setAdmin(user.getId(), false);
        String response = "Пользователь " + helper.userLink(user) + " освобождён от полномочий админа";
        helper.replyDelete(response, message);
        notifier.notifyNoAdmin(user.getId(), message.from());
    }

    @Admin
    @Respond
    @MessageListener(filter = "/gban")
    public void gban(Message message) {

        User user = helper.getTargetUser(message);
        userService.setUserAccessLevel(user.getId(), "ban");
        String response = helper.userLink(user) + " заблокирован во всех чатах";
        helper.replyDelete(response, message);
        notifier.notifyGban(user.getId(), message.from());
    }

    @Admin
    @Respond
    @MessageListener(filter = "/close")
    public void close(Message message) {

        settingsService.setRegistrationOpen(false);
        String response = "Регистрация новых пользователей временно приостановлена";
        helper.replyDelete(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/open")
    public void open(Message message) {

        settingsService.setRegistrationOpen(true);
        String response = "Регистрация новых пользователей возобновлена";
        helper.replyDelete(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/autokickon")
    public void autoKickOn(Message message) {

        settingsService.setAutoKickOn(true);
        String response = "Автокик молчунов включён";
        helper.replyDelete(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/autokickoff")
    public void autoKickOff(Message message) {

        settingsService.setAutoKickOn(false);
        String response = "Автокик молчунов выключен";
        helper.replyDelete(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/maxdays & text")
    public void setMaxDays(Message message) {

        String arg = helper.getSingleArg(message.text());
        try {

            int days = Integer.parseInt(arg);
            settingsService.setMaxDays(days);
            String response = "Максимальное время молчания - " + days + " cуток.";
            helper.replyDelete(response, message);

        } catch (NumberFormatException ex) {
            throw new ZwolabotException("'" + arg + "' не является валидным числом.");
        }
    }

    @Admin
    @Respond
    @MessageListener(filter = "/settings")
    public void settings(Message message) {

        String response = settingsService.getSettings().toString();
        helper.replyDelete(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/guser")
    public void getUserInfo(Message message) {

        User user = helper.getTargetUser(message);
        user = userService.getUser(user.getId());
        String response = user.toString();
        helper.replyDelete(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/help")
    public void help(Message message) {

        String response = "Для всех:\n" +
                "\n" +
                "/info - информация об уровне доступа и список чатов.\n" +
                "\n" +
                "Для модераторов чатов:\n" +
                "\n" +
                "/ban @юзернейм причина - забанить\n" +
                "/unban @юзернейм - разбанить\n" +
                "/warn @юзернейм - предупреждение\n" +
                "/clear @юзернейм - снять варны\n" +
                "/user @юзернейм - посмотреть кол во варнов\n" +
                "\n" +
                "Для админов\n" +
                "\n" +
                "/promote @юзернейм - сделать модером чата\n" +
                "/demote @юзернейм - убрать модера чата\n" +
                "/addchat уровень - добавить новый чат с указанным уровнем доступа\n" +
                "/update - обновить информацию о чате\n" +
                "/setlevel @юзернейм уровень - присвоить человеку уровень доступа\n" +
                "/gban - глобальный бан\n" +
                "/settings - настройки\n" +
                "/open - открыть регистрацию новичков\n" +
                "/close - закрыть регистрацию новичков\n" +
                "/autokickon - включить автокик раз в день\n" +
                "/autokickoff - выключиить автокик раз в день\n" +
                "/maxdays число - сколько дней можно молчать\n" +
                "/guser @юзернейм - инфа о пользователе\n" +
                "/chatinfo - инфа о чате\n" +
                "/help - это сообщение\n" +
                "/filter фраза - запретить фразу\n" +
                "/filter - запретить стикер\n" +
                "/unfilter - разрешить фразу или стикер\n" +
                "/packfilter - запретить пак\n" +
                "/packunfilter - разрешить пак\n" +
                "/badwords - запрещённые слова\n" +
                "/badpacks - запрещённые паки\n" +
                "/badstickers - запрещённые стики\n" +
                "/chatlistpic + фото - установить картинку для списка чатов\n" +
                "/removechatlistpic - удалить картинку для списка чатов\n" +
                "\n" +
                "Для верховного:\n" +
                "/admin @юзернейм - сделать админом\n" +
                "/noadmin @юзернейм - сделать не админом";
        helper.replyDelete(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/chatinfo")
    public void getChatInfo(Message message) {

        String response = chatService.getChat(message.chat().id()).toString();
        helper.replyDelete(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/chatlistpic & photo")
    public void chatListPic(Message message) {

        String fileId = message.photo()[0].fileId();
        settingsService.setChatListPicFileId(fileId);
        String response = "Новая картинка для списка чатов установлена";
        helper.replyDelete(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/removechatlistpic")
    public void removeChatListPic(Message message) {

        settingsService.setChatListPicFileId(null);
        String response = "Список чатов теперь отображается без картинки";
        helper.replyDelete(response, message);
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

        helper.delete(message.chat().id(), message.messageId());
    }

    @Autowired
    public BaseModule(UserService userService, ChatService chatService, SettingsService settingsService, ControllerHelper helper, TgSender tgSender, Notifier notifier) {
        this.userService = userService;
        this.chatService = chatService;
        this.settingsService = settingsService;
        this.helper = helper;
        this.tgSender = tgSender;
        this.notifier = notifier;
    }
}

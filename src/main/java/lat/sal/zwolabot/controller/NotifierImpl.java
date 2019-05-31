package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lat.sal.zwolabot.telegram.TgSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotifierImpl implements Notifier {

    private TgSender tgSender;

    @Override
    public void notifyAdmin(long id, User by) {

        String text = userLink(by) + " сделал вас администратором.";
        sendMessage(id, text);
    }

    @Override
    public void notifyNoAdmin(long id, User by) {

        String text = userLink(by) + " освободил вас от полномочий администратора.";
        sendMessage(id, text);

    }

    @Override
    public void notifyModerator(long id, Chat chat, User by) {

        String text = userLink(by) + " сделал вас модератором чата " + chat.title();
        sendMessage(id, text);
    }

    @Override
    public void notifyNoModerator(long id, Chat chat, User by) {

        String text = userLink(by) + " освободил вас от полномочий модератора чата " + chat.title();
        sendMessage(id, text);
    }

    @Override
    public void notifyNewLevel(long id, String level, User by) {

        String text = userLink(by) + " назначил вам уровень доступа *" + level + "*" +
                "\nДля большей информации используйте команду /info или Доступные чаты";
        sendMessage(id, text);
    }

    @Override
    public void notifyBan(long id, Chat chat, String reason, User by) {

        String text = userLink(by) + " забанил вас в чате " + chat.title() + " по причине: _" + reason + "_";
        sendMessage(id, text);
    }

    @Override
    public void notifyUnban(long id, Chat chat, User by) {

        String text = userLink(by) + " разбанил вас в чате " + chat.title();
        sendMessage(id, text);

    }

    @Override
    public void notifyGban(long id, User by) {

        String text = userLink(by) + " выписал вас из русских.";
        sendMessage(id, text);
    }

    private String userLink(User user) {

        String result = user.firstName();
        if (user.lastName() != null)
            result += " " + user.lastName();

        if (user.username() != null)
            result = "[" + result + "](https://t.me/" + user.username() + ")";

        return result;
    }

    private void sendMessage(long id, String text) {

        SendMessage sendMessage = new SendMessage(id, text)
                .parseMode(ParseMode.Markdown)
                .disableWebPagePreview(true);

        tgSender.executeOrLog(sendMessage);
    }

    @Autowired
    public NotifierImpl(TgSender tgSender) {
        this.tgSender = tgSender;
    }
}

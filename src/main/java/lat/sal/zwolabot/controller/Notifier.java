package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

public interface Notifier {

    void notifyAdmin(long id, User by);
    void notifyNoAdmin(long id, User by);

    void notifyModerator(long id, Chat chat, User by);
    void notifyNoModerator(long id, Chat chat, User by);

    void notifyNewLevel(long id, String level, User by);

    void notifyBan(long id, Chat chat, String reason, User by);
    void notifyUnban(long id, Chat chat, User by);

    void notifyGban(long id, User by);
}

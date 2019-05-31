package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

public class NotifierImpl implements Notifier {
    @Override
    public void notifyAdmin(long id, User by) {

    }

    @Override
    public void notifyNoAdmin(long id, User by) {

    }

    @Override
    public void notifyModerator(long id, Chat chat, User by) {

    }

    @Override
    public void notifyNoModerator(long id, Chat chat, User by) {

    }

    @Override
    public void notifyNewLevel(long id, String level, User by) {

    }

    @Override
    public void notifyBan(long id, Chat chat, String reason, User by) {

    }

    @Override
    public void notifyUnban(long id, Chat chat, User by) {

    }

    @Override
    public void notifyGban(long id, User by) {

    }
}

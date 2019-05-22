package lat.sal.zwolabot.service;

import lat.sal.zwolabot.entity.Chat;

import java.util.Date;

public interface ChatService {

    void addChat(long id, String accessLevel);

    void updateChat(long id);

    void onMessage(long chatId, long userId, Date date);

    // butler

    void setModerator(long chatId, long userId, boolean moderator);

    int warn(long chatId, long userId);

    void ban(long chatId, long userId, String note);

    void unban(long chatId, long userId);

    void clearWarns(long chatId, long userId);
}

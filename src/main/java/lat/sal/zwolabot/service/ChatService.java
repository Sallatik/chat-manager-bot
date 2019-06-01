package lat.sal.zwolabot.service;

import lat.sal.zwolabot.entity.Chat;
import lat.sal.zwolabot.entity.ChatUser;

import java.util.Date;
import java.util.List;

public interface ChatService {

    void addChat(long id, String accessLevel);

    void updateChat(long id);

    void onMessage(long chatId, long userId, long timestamp);

    // butler

    void setModerator(long chatId, long userId, boolean moderator);

    int warn(long chatId, long userId);

    void ban(long chatId, long userId, String note);

    void unban(long chatId, long userId);

    void clearWarns(long chatId, long userId);

    ChatUser getChatUser(long chatId, long userId);

    Chat getChat(long id);

    boolean isModerator(long chatId, long userId);
}

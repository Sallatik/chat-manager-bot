package lat.sal.zwolabot.dao;

import lat.sal.zwolabot.entity.ChatUser;

public interface ChatUserDAO {

    ChatUser getChatUser(long chatId, long userId);

    void saveChatUser(ChatUser chatUser);
}

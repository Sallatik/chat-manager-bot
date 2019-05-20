package lat.sal.zwolabot.dao;

import lat.sal.zwolabot.entity.Chat;

public interface ChatDAO {

    Chat getChat(long id);

    void saveChat(Chat chat);
}

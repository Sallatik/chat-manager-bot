package lat.sal.zwolabot.dao;

import lat.sal.zwolabot.entity.Chat;

import java.util.List;

public interface ChatDAO {

    Chat getChat(long id);

    void saveChat(Chat chat);

    List<Chat> getAllChats();
}

package lat.sal.zwolabot.telegram;

import lat.sal.zwolabot.entity.Chat;

public interface TelegramFacade {

    void kick(long chatId, long userId);

    void ban(long chatId, long userId);

    void unban(long chatId, long userId);

    Chat getChat(long id);

    void deleteMessage(long chatId, int messageId);

    void sendMessage(String text, long chatId);
}

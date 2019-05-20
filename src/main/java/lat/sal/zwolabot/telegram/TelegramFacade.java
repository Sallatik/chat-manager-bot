package lat.sal.zwolabot.telegram;

public interface TelegramFacade {

    void kick(long chatId, long userId);

    void ban(long chatId, long userId);

    void unban(long chatId, long userId);
}

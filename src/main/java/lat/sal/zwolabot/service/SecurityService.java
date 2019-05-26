package lat.sal.zwolabot.service;

public interface SecurityService {

    void requireModerator(long chatId, long userId);

    void requireAdmin(long userId);

    void requireRootAdmin(long userId);
}

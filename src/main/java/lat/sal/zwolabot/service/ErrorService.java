package lat.sal.zwolabot.service;

import lat.sal.zwolabot.entity.AccessLevel;
import lat.sal.zwolabot.entity.Chat;
import lat.sal.zwolabot.entity.User;

public interface ErrorService {

    void requireNonNull(User user);

    void requireNonNull(Chat chat);

    void requireNonNull(AccessLevel accessLevel);

    void requireNull(User user);

    void requireNull(Chat chat);

    void requireAdmin(long id);

    void reqireAdminOrModerator(long chatId, long userId);
}

package lat.sal.zwolabot.service;

import lat.sal.zwolabot.entity.AccessLevel;
import lat.sal.zwolabot.entity.Chat;
import lat.sal.zwolabot.entity.User;

interface ErrorManager {

    void requireNonNull(User user);

    void requireNonNull(Chat chat);

    void requireNonNull(AccessLevel accessLevel);

    void requireNull(User user);

    void requireNull(Chat chat);

    void requireRegistrationOpen(boolean registrationOpen);
}

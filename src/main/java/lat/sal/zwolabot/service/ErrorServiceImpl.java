package lat.sal.zwolabot.service;

import lat.sal.zwolabot.ZwolabotException;
import lat.sal.zwolabot.dao.ChatUserDAO;
import lat.sal.zwolabot.dao.FilterDAO;
import lat.sal.zwolabot.dao.SettingsDAO;
import lat.sal.zwolabot.dao.UserDAO;
import lat.sal.zwolabot.entity.AccessLevel;
import lat.sal.zwolabot.entity.Chat;
import lat.sal.zwolabot.entity.ChatUser;
import lat.sal.zwolabot.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ErrorServiceImpl implements ErrorService {

    private ChatUserDAO chatUserDAO;
    private UserDAO userDAO;
    private SettingsDAO settingsDAO;

    @Override
    public void requireNonNull(User user) {

        if (user == null)
            throw new ZwolabotException("Неизвестный пользователь");
    }

    @Override
    public void requireNonNull(Chat chat) {

        if (chat == null)
            throw new ZwolabotException("Неизвестный чат");
    }

    @Override
    public void requireNonNull(AccessLevel accessLevel) {

        if (accessLevel == null)
            throw new ZwolabotException("Несуществующий уровень доступа");
    }

    @Override
    public void requireNull(User user) {

        if (user != null)
            throw new ZwolabotException("Пользователь уже зарешгистрирован");
    }

    @Override
    public void requireNull(Chat chat) {

        if (chat != null)
            throw new ZwolabotException("Чат уже существует");
    }

    @Override
    public void requireRoot(long id) {

        if (!(isAdmin(id) && isRoot(id)))
            throw new ZwolabotException("Вы не главный админ.");
    }

    @Override
    public void requireAdmin(long id) {

        if (!isAdmin(id))
            throw new ZwolabotException("Вы не админ");
    }

    @Override
    public void reqireAdminOrModerator(long chatId, long userId) {

        if (!(isAdmin(userId) || isModerator(chatId, userId)))
            throw new ZwolabotException("Вы не модератор");
    }

    @Override
    public void requireRegistrationOpen() {

        if (!settingsDAO.isRegistrationOpen())
            throw new ZwolabotException("Регистрация новых пользователей временно закрыта.\n" +
                    "Попробуйте позже.");
    }

    private boolean isRoot(long id) {

        return id == 209601261L;
    }

    private boolean isModerator(long chatId, long userId) {

        ChatUser chatUser = chatUserDAO.getChatUser(chatId, userId);
        return chatUser != null && chatUser.isModerator();
    }

    private boolean isAdmin(long id) {

        User user = userDAO.getUser(id);
        return user != null && user.getStatus().isAdmin();
    }

    @Autowired
    public ErrorServiceImpl(ChatUserDAO chatUserDAO, UserDAO userDAO, SettingsDAO settingsDAO) {
        this.chatUserDAO = chatUserDAO;
        this.userDAO = userDAO;
        this.settingsDAO = settingsDAO;
    }
}

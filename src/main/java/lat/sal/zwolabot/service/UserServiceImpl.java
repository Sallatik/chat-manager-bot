package lat.sal.zwolabot.service;

import lat.sal.zwolabot.dao.AccessLevelDAO;
import lat.sal.zwolabot.dao.ChatDAO;
import lat.sal.zwolabot.dao.SettingsDAO;
import lat.sal.zwolabot.dao.UserDAO;
import lat.sal.zwolabot.entity.*;
import lat.sal.zwolabot.telegram.TelegramFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
class UserServiceImpl implements UserService {

    private UserDAO userDAO;
    private AccessLevelDAO accessLevelDAO;
    private ChatDAO chatDAO;
    private SettingsDAO settingsDAO;
    private ErrorManager errorManager;
    private TelegramFacade telegramFacade;

    @Value("${zwolabot.default-access-level}")
    private String defaultAccessLevel;
    @Value("${zwolabot.ban-access-level}")
    private String banAccessLevel;
    @Value("${zwolabot.root-user-id}")
    private long rootUserId;

    @Override
    @Transactional
    public void addUser(User user) {

        Settings settings = settingsDAO.getSettings();
        errorManager.requireRegistrationOpen(settings != null && settings.isRegistrationOpen());
        errorManager.requireNull(userDAO.getUser(user.getId()));
        AccessLevel accessLevel = accessLevelDAO.getAccessLevel(defaultAccessLevel);
        errorManager.requireNonNull(accessLevel);

        user.setAccessLevel(accessLevel);
        userDAO.saveUser(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) {

        User persistent = userDAO.getUser(user.getId());

        if (persistent != null)
            persistent.update(user);
    }

    public void setAccessLevel(long id, String level) {

        User user = userDAO.getUser(id);
        AccessLevel accessLevel = accessLevelDAO.getAccessLevel(level);

        errorManager.requireNonNull(user);
        errorManager.requireNonNull(accessLevel);

        user.setAccessLevel(accessLevel);

        for (Chat chat : chatDAO.getAllChats()) {
            if (chat.getAccessLevel().getValue() > accessLevel.getValue())
                telegramFacade.ban(chat.getId(), user.getId());
            else
                telegramFacade.unban(chat.getId(), user.getId());
        }
    }

    @Override
    @Transactional
    public void setUserAccessLevel(long id, String accessLevel) {
        setAccessLevel(id, accessLevel);
    }

    @Override
    @Transactional
    public void gban(long id) {
        setAccessLevel(id, banAccessLevel);
    }

    @Override
    @Transactional
    public void setAdmin(long id, boolean admin) {

        User user = userDAO.getUser(id);
        errorManager.requireNonNull(user);

        user.getStatus().setAdmin(admin);
    }

    @Override
    @Transactional
    public LevelAndChats getLevelAndAvailableChats(long id) {

        User user = userDAO.getUser(id);
        errorManager.requireNonNull(user);

        Comparator<Chat> comparator = Comparator.comparingInt(chat -> chat.getAccessLevel().getValue());
        comparator = comparator.reversed();
        comparator = comparator.thenComparing(Chat::getTitle);

        AccessLevel level = user.getAccessLevel();
        List<Chat> chats = chatDAO.getAllChats()
                .stream()
                .filter(chat -> chat.getAccessLevel().getValue() <= level.getValue())
                .collect(Collectors.toList());

        List<Chat> bannedChats = user.getChats()
                .stream()
                .filter(ChatUser::isBanned)
                .map(ChatUser::getChat)
                .collect(Collectors.toList());

        chats.removeAll(bannedChats);

        chats.sort(comparator);
        return new LevelAndChats(level, chats);
    }

    @Override
    @Transactional
    public User getUserByUsername(String username) {

        User user = userDAO.getUserByUsername(username);
        errorManager.requireNonNull(user);
        return user;
    }

    @Override
    @Transactional
    public User getUser(long id) {

        User user = userDAO.getUser(id);
        errorManager.requireNonNull(user);
        return user;
    }

    @Override
    public boolean isRoot(long id) {
        return id == rootUserId;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAdmin(long id) {

        User user = userDAO.getUser(id);
        return user != null && user.getStatus().isAdmin();
    }

    @Autowired
    public UserServiceImpl(UserDAO userDAO, AccessLevelDAO accessLevelDAO, ChatDAO chatDAO, SettingsDAO settingsDAO, ErrorManager errorManager, TelegramFacade telegramFacade) {
        this.userDAO = userDAO;
        this.accessLevelDAO = accessLevelDAO;
        this.chatDAO = chatDAO;
        this.settingsDAO = settingsDAO;
        this.errorManager = errorManager;
        this.telegramFacade = telegramFacade;
    }
}

package lat.sal.zwolabot.service;

import lat.sal.zwolabot.dao.AccessLevelDAO;
import lat.sal.zwolabot.dao.ChatDAO;
import lat.sal.zwolabot.dao.UserDAO;
import lat.sal.zwolabot.entity.AccessLevel;
import lat.sal.zwolabot.entity.Chat;
import lat.sal.zwolabot.entity.User;
import lat.sal.zwolabot.telegram.TelegramFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;
    private AccessLevelDAO accessLevelDAO;
    private ChatDAO chatDAO;
    private ErrorService errorService;
    private TelegramFacade telegramFacade;

    @Value("${zwolabot.default-access-level}")
    private String defaultAccessLevel;

    @Override
    @Transactional
    public void addUser(User user) {

        errorService.requireNull(userDAO.getUser(user.getId()));
        AccessLevel accessLevel = accessLevelDAO.getAccessLevel(defaultAccessLevel);
        errorService.requireNonNull(accessLevel);

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

    @Override
    @Transactional
    public void setUserAccessLevel(long id, String level) {

        User user = userDAO.getUser(id);
        AccessLevel accessLevel = accessLevelDAO.getAccessLevel(level);

        errorService.requireNonNull(user);
        errorService.requireNonNull(accessLevel);

        user.setAccessLevel(accessLevel);

        for (Chat chat : chatDAO.getAllChats()) {
            if (chat.getAccessLevel().getValue() > accessLevel.getValue())
                telegramFacade.kick(chat.getId(), user.getId());
        }
    }

    @Override
    @Transactional
    public void setAdmin(long id, boolean admin) {

        User user = userDAO.getUser(id);
        errorService.requireNonNull(user);

        user.getStatus().setAdmin(admin);
    }

    @Override
    @Transactional
    public LevelAndChats getLevelAndAvailableChats(long id) {

        User user = userDAO.getUser(id);
        errorService.requireNonNull(user);

        AccessLevel level = user.getAccessLevel();
        List<Chat> chats = chatDAO.getAllChats()
                .stream()
                .filter(chat -> chat.getAccessLevel().getValue() <= level.getValue())
                .collect(Collectors.toList());

        return new LevelAndChats(level, chats);
    }

    @Override
    @Transactional
    public User getUserByUsername(String username) {

        User user = userDAO.getUserByUsername(username);
        errorService.requireNonNull(user);
        return user;
    }

    @Autowired
    public UserServiceImpl(UserDAO userDAO, AccessLevelDAO accessLevelDAO, ChatDAO chatDAO, ErrorService errorService, TelegramFacade telegramFacade) {
        this.userDAO = userDAO;
        this.accessLevelDAO = accessLevelDAO;
        this.chatDAO = chatDAO;
        this.errorService = errorService;
        this.telegramFacade = telegramFacade;
    }
}

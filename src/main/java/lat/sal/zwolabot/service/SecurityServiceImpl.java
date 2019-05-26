package lat.sal.zwolabot.service;

import lat.sal.zwolabot.ZwolabotException;
import lat.sal.zwolabot.dao.ChatUserDAO;
import lat.sal.zwolabot.dao.UserDAO;
import lat.sal.zwolabot.entity.ChatUser;
import lat.sal.zwolabot.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    private UserDAO userDAO;
    private ChatUserDAO chatUserDAO;

    @Override
    public void requireRootAdmin(long id) {

        if (!(isAdmin(id) && isRoot(id)))
            throw new ZwolabotException("Вы не главный админ.");
    }

    @Override
    public void requireAdmin(long id) {

        if (!isAdmin(id))
            throw new ZwolabotException("Вы не админ");
    }

    @Override
    public void requireModerator(long chatId, long userId) {

        if (!(isAdmin(userId) || isModerator(chatId, userId)))
            throw new ZwolabotException("Вы не модератор");
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
    public SecurityServiceImpl(UserDAO userDAO, ChatUserDAO chatUserDAO) {
        this.userDAO = userDAO;
        this.chatUserDAO = chatUserDAO;
    }
}

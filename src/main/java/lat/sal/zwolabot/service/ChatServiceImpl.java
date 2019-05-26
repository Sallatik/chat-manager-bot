package lat.sal.zwolabot.service;

import lat.sal.zwolabot.dao.AccessLevelDAO;
import lat.sal.zwolabot.dao.ChatDAO;
import lat.sal.zwolabot.dao.ChatUserDAO;
import lat.sal.zwolabot.dao.UserDAO;
import lat.sal.zwolabot.entity.AccessLevel;
import lat.sal.zwolabot.entity.Chat;
import lat.sal.zwolabot.entity.ChatUser;
import lat.sal.zwolabot.entity.User;
import lat.sal.zwolabot.telegram.TelegramFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ChatServiceImpl implements ChatService {

    private ChatDAO chatDAO;
    private UserDAO userDAO;
    private AccessLevelDAO accessLevelDAO;
    private ErrorManager errorManager;
    private TelegramFacade telegramFacade;
    private ChatUserDAO chatUserDAO;

    @Override
    @Transactional
    public void addChat(long id, String level) {

        errorManager.requireNull(chatDAO.getChat(id));
        AccessLevel accessLevel = accessLevelDAO.getAccessLevel(level);
        errorManager.requireNonNull(accessLevel);

        Chat chat = telegramFacade.getChat(id);
        chat.setAccessLevel(accessLevel);

        chatDAO.saveChat(chat);
    }

    @Override
    @Transactional
    public void updateChat(long id) {

        Chat chat = telegramFacade.getChat(id);
        Chat persistent = chatDAO.getChat(id);
        errorManager.requireNonNull(persistent);
        persistent.update(chat);
    }

    @Override
    @Transactional
    public void onMessage(long chatId, long userId, long timestamp) {

        Chat chat = chatDAO.getChat(chatId);
        User user = userDAO.getUser(userId);

        if (chat == null)
            return; // unknown chat: do nothing

        if (user == null || user.getAccessLevel().getValue() < chat.getAccessLevel().getValue()) {
            telegramFacade.kick(chatId, userId);
            return;
        }

        ChatUser chatUser = getOrCreateChatUser(chat, user);
        chatUser.setLastMessage(timestamp);
    }

    private ChatUser getOrCreateChatUser(long chatId, long userId) {

        Chat chat = chatDAO.getChat(chatId);
        User user = userDAO.getUser(userId);

        errorManager.requireNonNull(chat);
        errorManager.requireNonNull(user);
        return getOrCreateChatUser(chat, user);
    }

    private ChatUser getOrCreateChatUser(Chat chat, User user) {

        ChatUser chatUser = chatUserDAO.getChatUser(chat.getId(), user.getId());

        if (chatUser == null) {
            chatUser = new ChatUser(chat, user);
            chatUserDAO.saveChatUser(chatUser);
        }

        return chatUser;
    }

    @Override
    @Transactional
    public void setModerator(long chatId, long userId, boolean moderator) {

        ChatUser chatUser = getOrCreateChatUser(chatId, userId);
        chatUser.setModerator(moderator);
    }

    @Override
    @Transactional
    public int warn(long chatId, long userId) {

        ChatUser chatUser = getOrCreateChatUser(chatId, userId);
        chatUser.setWarns(chatUser.getWarns() + 1);

        if (chatUser.getWarns() >= 3)  // todo: replace literal '3'
            ban(chatId, userId, "3 warns");

        return chatUser.getWarns();
    }

    @Override
    @Transactional
    public void ban(long chatId, long userId, String note) {

        ChatUser chatUser = getOrCreateChatUser(chatId, userId);
        chatUser.setBanned(true);
        chatUser.setNote(note);
        telegramFacade.ban(chatId, userId);
    }

    @Override
    @Transactional
    public void unban(long chatId, long userId) {

        ChatUser chatUser = getOrCreateChatUser(chatId, userId);
        chatUser.setBanned(false);
        chatUser.setWarns(0);
        telegramFacade.unban(chatId, userId);
    }

    @Override
    @Transactional
    public void clearWarns(long chatId, long userId) {

        ChatUser chatUser = getOrCreateChatUser(chatId, userId);
        chatUser.setWarns(0);
    }

    @Override
    @Transactional
    public ChatUser getChatUser(long chatId, long userId) {

        return getOrCreateChatUser(chatId, userId);
    }

    @Override
    @Transactional
    public Chat getChat(long id) {

        Chat chat = chatDAO.getChat(id);
        errorManager.requireNonNull(chat);
        return chat;
    }

    @Autowired
    public ChatServiceImpl(ChatDAO chatDAO, UserDAO userDAO, AccessLevelDAO accessLevelDAO, ErrorManager errorManager, TelegramFacade telegramFacade, ChatUserDAO chatUserDAO) {
        this.chatDAO = chatDAO;
        this.userDAO = userDAO;
        this.accessLevelDAO = accessLevelDAO;
        this.errorManager = errorManager;
        this.telegramFacade = telegramFacade;
        this.chatUserDAO = chatUserDAO;
    }
}

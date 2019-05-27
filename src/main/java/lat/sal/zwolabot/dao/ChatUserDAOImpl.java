package lat.sal.zwolabot.dao;

import lat.sal.zwolabot.entity.ChatUser;
import lat.sal.zwolabot.entity.ChatUserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
class ChatUserDAOImpl implements ChatUserDAO {

    private EntityManager entityManager;

    @Override
    public ChatUser getChatUser(long chatId, long userId) {
        return entityManager.find(ChatUser.class, new ChatUserId(chatId, userId));
    }

    @Override
    public void saveChatUser(ChatUser chatUser) {
        entityManager.persist(chatUser);
    }

    @Autowired
    public ChatUserDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}

package lat.sal.zwolabot.dao;

import lat.sal.zwolabot.entity.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class ChatDAOImpl implements ChatDAO {

    private EntityManager entityManager;

    @Override
    public Chat getChat(long id) {
        return entityManager.find(Chat.class, id);
    }

    @Override
    public void saveChat(Chat chat) {
        entityManager.persist(chat);
    }

    @Override
    public List<Chat> getAllChats() {

        return entityManager.createQuery("from Chat", Chat.class)
                .getResultList();
    }

    @Autowired
    public ChatDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}

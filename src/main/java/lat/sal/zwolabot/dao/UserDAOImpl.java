package lat.sal.zwolabot.dao;

import lat.sal.zwolabot.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {

    private EntityManager entityManager;

    @Override
    public User getUser(long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void saveUser(User user) {
        entityManager.persist(user);
    }

    @Override
    public User getUserByUsername(String username) {

        List<User> users = entityManager.createQuery("from User where username like :username", User.class)
                .setParameter("username", username)
                .getResultList();

        if (users.size() == 1)
            return users.get(0);
        else
            return null;
    }

    @Autowired
    public UserDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}

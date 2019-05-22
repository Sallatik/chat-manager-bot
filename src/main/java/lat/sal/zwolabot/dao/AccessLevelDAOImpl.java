package lat.sal.zwolabot.dao;

import lat.sal.zwolabot.entity.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Repository
public class AccessLevelDAOImpl implements AccessLevelDAO {

    private EntityManager entityManager;

    @Override
    public AccessLevel getAccessLevel(String name) {
        return entityManager.find(AccessLevel.class, name);
    }

    @Override
    public void saveAccessLevel(AccessLevel accessLevel) {
        entityManager.persist(accessLevel);
    }

    public AccessLevelDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}

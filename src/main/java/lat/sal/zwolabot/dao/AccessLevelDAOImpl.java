package lat.sal.zwolabot.dao;

import lat.sal.zwolabot.entity.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

@Repository
class AccessLevelDAOImpl implements AccessLevelDAO {

    private EntityManager entityManager;

    @Override
    public AccessLevel getAccessLevel(String name) {

        List<AccessLevel> result = entityManager
                        .createQuery("from AccessLevel where name like :name", AccessLevel.class)
                        .setParameter("name", name)
                        .getResultList();

        if (result.size() == 1)
            return result.get(0);
        else
            return null;
    }

    @Override
    public void saveAccessLevel(AccessLevel accessLevel) {
        entityManager.persist(accessLevel);
    }

    public AccessLevelDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}

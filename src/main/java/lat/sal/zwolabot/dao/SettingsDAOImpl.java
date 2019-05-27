package lat.sal.zwolabot.dao;

import lat.sal.zwolabot.entity.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

@Repository
class SettingsDAOImpl implements SettingsDAO {

    private EntityManager entityManager;

    @Override
    public Settings getSettings() {

        return entityManager.find(Settings.class, 1);
    }

    @Override
    public void saveSettings(Settings settings) {

        entityManager.merge(settings);
    }

    @Autowired
    public SettingsDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}

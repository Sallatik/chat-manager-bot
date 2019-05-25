package lat.sal.zwolabot.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;

@Repository
public class SettingsDAOImpl implements SettingsDAO {

    private Jedis jedis;

    @Value("${zwolabot.registration-open-key:registration}")
    private String registrationOpenKey;

    private boolean registrationOpen;

    @Override
    public boolean isRegistrationOpen() {
        return registrationOpen;
    }

    @Override
    public void setRegistrationOpen(boolean registrationOpen) {

        jedis.set(registrationOpenKey, String.valueOf(registrationOpen));
        this.registrationOpen = registrationOpen;
    }

    @PostConstruct
    public void init() {

        String registrationOpenString = jedis.get(registrationOpenKey);

        if (registrationOpenString != null)
            registrationOpen = Boolean.parseBoolean(registrationOpenString);
    }

    @Autowired
    public SettingsDAOImpl(Jedis jedis) {
        this.jedis = jedis;
    }
}

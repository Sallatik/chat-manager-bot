package lat.sal.zwolabot.service;

import lat.sal.zwolabot.dao.RedisDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsServiceImpl implements SettingsService {

    private RedisDAO redisDAO;

    @Override
    public void setRegistrationOpen(boolean registrationOpen) {

        redisDAO.setRegistrationOpen(registrationOpen);
    }

    @Autowired
    public SettingsServiceImpl(RedisDAO redisDAO) {
        this.redisDAO = redisDAO;
    }
}

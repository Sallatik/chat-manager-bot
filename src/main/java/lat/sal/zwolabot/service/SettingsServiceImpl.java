package lat.sal.zwolabot.service;

import lat.sal.zwolabot.dao.SettingsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsServiceImpl implements SettingsService {

    private SettingsDAO settingsDAO;

    @Override
    public void setRegistrationOpen(boolean registrationOpen) {

        settingsDAO.setRegistrationOpen(registrationOpen);
    }

    @Autowired
    public SettingsServiceImpl(SettingsDAO settingsDAO) {
        this.settingsDAO = settingsDAO;
    }
}

package lat.sal.zwolabot.config;

import lat.sal.zwolabot.dao.AccessLevelDAO;
import lat.sal.zwolabot.dao.SettingsDAO;
import lat.sal.zwolabot.entity.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DBInitializerBean {

    private AccessLevelDAO accessLevelDAO;
    private SettingsDAO settingsDAO;

    private void initAccessLevels() {

    }

    private void initSettings() {

        if (settingsDAO.getSettings() == null) {

            Settings settings = new Settings();
            settings.setRegistrationOpen(true);
            settingsDAO.saveSettings(settings);
        }
    }

    @Transactional
    public void init() {

        initSettings();
        initAccessLevels();
    }

    @Autowired
    public DBInitializerBean(AccessLevelDAO accessLevelDAO, SettingsDAO settingsDAO) {
        this.accessLevelDAO = accessLevelDAO;
        this.settingsDAO = settingsDAO;
    }
}

package lat.sal.zwolabot.service;

import lat.sal.zwolabot.dao.SettingsDAO;
import lat.sal.zwolabot.entity.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class SettingsServiceImpl implements SettingsService {

    private SettingsDAO settingsDAO;

    @Override
    @Transactional(readOnly = true)
    public Settings getSettings() {
        return getOrCreateSettings();
    }

    @Override
    @Transactional
    public void setRegistrationOpen(boolean registrationOpen) {

        Settings settings = getOrCreateSettings();
        settings.setRegistrationOpen(registrationOpen);
    }

    private Settings getOrCreateSettings() {

        Settings settings = settingsDAO.getSettings();

        if (settings == null) {
            settings = new Settings();
            settingsDAO.saveSettings(settings);
        }

        return settings;
    }

    @Autowired
    public SettingsServiceImpl(SettingsDAO settingsDAO) {
        this.settingsDAO = settingsDAO;
    }
}

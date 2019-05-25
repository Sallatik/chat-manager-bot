package lat.sal.zwolabot.service;

import lat.sal.zwolabot.entity.Settings;

public interface SettingsService {

    Settings getSettings();

    void setRegistrationOpen(boolean registrationOpen);

}

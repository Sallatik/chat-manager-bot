package lat.sal.zwolabot.dao;

import lat.sal.zwolabot.entity.Settings;

public interface SettingsDAO {

    Settings getSettings();

    void saveSettings(Settings settings);
}

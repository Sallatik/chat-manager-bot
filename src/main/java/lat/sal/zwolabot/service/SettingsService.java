package lat.sal.zwolabot.service;

import lat.sal.zwolabot.entity.Settings;

public interface SettingsService {

    Settings getSettings();

    void setRegistrationOpen(boolean registrationOpen);

    void setAutoKickOn(boolean autoKickOn);

    void setMaxDays(int maxDays);

    void setChatListPicFileId(String chatListPicFileId);
}

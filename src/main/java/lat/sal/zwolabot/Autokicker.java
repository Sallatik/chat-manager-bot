package lat.sal.zwolabot;

import lat.sal.zwolabot.controller.annotation.Admin;
import lat.sal.zwolabot.dao.ChatDAO;
import lat.sal.zwolabot.dao.SettingsDAO;
import lat.sal.zwolabot.entity.Chat;
import lat.sal.zwolabot.entity.ChatUser;
import lat.sal.zwolabot.entity.Settings;
import lat.sal.zwolabot.service.ChatService;
import lat.sal.zwolabot.service.SettingsService;
import lat.sal.zwolabot.telegram.TelegramFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class Autokicker {

    private SettingsDAO settingsDAO;
    private ChatDAO chatDAO;
    private TelegramFacade telegramFacade;

    @Scheduled(cron = "0 0 18 * * ?")
    @Transactional(readOnly = true)
    public void autokick() {
        Settings settings = settingsDAO.getSettings();
        if (!settings.isAutoKickOn())
            return;

        long max = TimeUnit.DAYS.toMillis(settings.getMaxIdleDays());
        long now = System.currentTimeMillis();

        for (Chat chat : chatDAO.getAllChats()) {
            for (ChatUser chatUser : chat.getUsers()) {
                System.out.println("checking user " + chatUser.getUser().getFirstName());
                long last = chatUser.getLastMessage() * 1000;
                if (last != 0 && now - last > max) {
                    try {
                        telegramFacade.kick(chat.getId(), chatUser.getUser().getId());
                    } catch (ZwolabotException e) {}
                }
            }
        }
    }

    @Autowired
    public Autokicker(SettingsDAO settingsDAO, ChatDAO chatDAO, TelegramFacade telegramFacade) {
        this.settingsDAO = settingsDAO;
        this.chatDAO = chatDAO;
        this.telegramFacade = telegramFacade;
    }
}

package lat.sal.zwolabot.service;

import lat.sal.zwolabot.ZwolabotException;
import lat.sal.zwolabot.entity.AccessLevel;
import lat.sal.zwolabot.entity.Chat;
import lat.sal.zwolabot.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ErrorManagerImpl implements ErrorManager {

    private SettingsService settingsService;

    @Override
    public void requireNonNull(User user) {

        if (user == null)
            throw new ZwolabotException("Неизвестный пользователь");
    }

    @Override
    public void requireNonNull(Chat chat) {

        if (chat == null)
            throw new ZwolabotException("Неизвестный чат");
    }

    @Override
    public void requireNonNull(AccessLevel accessLevel) {

        if (accessLevel == null)
            throw new ZwolabotException("Несуществующий уровень доступа");
    }

    @Override
    public void requireNull(User user) {

        if (user != null)
            throw new ZwolabotException("Пользователь уже зарешгистрирован");
    }

    @Override
    public void requireNull(Chat chat) {

        if (chat != null)
            throw new ZwolabotException("Чат уже существует");
    }

    @Override
    public void requireRegistrationOpen() {

        if (!settingsService.getSettings().isRegistrationOpen())
            throw new ZwolabotException("Регистрация новых пользователей временно закрыта.\n" +
                    "Попробуйте позже.");
    }



    @Autowired
    public ErrorManagerImpl(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}

package lat.sal.zwolabot.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Settings {

    @Id
    private int id = 1;

    private boolean registrationOpen;
    private boolean autoKickOn;
    private int maxIdleDays = 14;

    public boolean isRegistrationOpen() {
        return registrationOpen;
    }

    public void setRegistrationOpen(boolean registrationOpen) {
        this.registrationOpen = registrationOpen;
    }

    public int getMaxIdleDays() {
        return maxIdleDays;
    }

    public void setMaxIdleDays(int maxIdleDays) {
        this.maxIdleDays = maxIdleDays;
    }

    public boolean isAutoKickOn() {
        return autoKickOn;
    }

    public void setAutoKickOn(boolean autoKickOn) {
        this.autoKickOn = autoKickOn;
    }

    public Settings() { }

    @Override
    public String toString() {
        return "*Текущие настройки:* \n" +
                "Регистрация: " + (registrationOpen ? "открыта" : "закрыта") + "\n" +
                "Автокик молчунов: " + (autoKickOn ? "включён" : "выключен") + "\n" +
                "Макисмальное время молчания: " + maxIdleDays + " cуток";
    }
}

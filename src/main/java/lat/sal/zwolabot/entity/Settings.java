package lat.sal.zwolabot.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Settings {

    @Id
    private int id = 1;

    private boolean registrationOpen;

    public boolean isRegistrationOpen() {
        return registrationOpen;
    }

    public void setRegistrationOpen(boolean registrationOpen) {
        this.registrationOpen = registrationOpen;
    }

    public Settings() { }

    @Override
    public String toString() {
        return "*Текущие настройки:* \n" +
                "Регистрация: " + (registrationOpen ? "открыта" : "закрыта");
    }
}

package lat.sal.zwolabot.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    private long id;

    private String firstName;
    private String lastName;
    private String username;

    @OneToOne(cascade = CascadeType.ALL)
    private UserStatus status;

    @ManyToOne
    private AccessLevel accessLevel;

    @OneToMany(mappedBy = "user")
    private List<ChatUser> chats;

    public void update(User user) {

        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setUsername(user.getUsername());
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User(com.pengrad.telegrambot.model.User user) {

        this.id = user.id();
        this.firstName = user.firstName();
        this.lastName = user.lastName();
        this.username = user.username();
        this.status = new UserStatus();
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public List<ChatUser> getChats() {
        return chats;
    }

    public void setChats(List<ChatUser> chats) {
        this.chats = chats;
    }

    public User() {}

    @Override
    public String toString() {

        StringBuilder resultBuilder = new StringBuilder("*Пользователь:* _" + id + "_\n");
        resultBuilder.append("Имя: " + firstName + "\n");
        if (lastName != null)
            resultBuilder.append("Фамилия: " + lastName + "\n");
        if (username != null)
            resultBuilder.append("Ник: @" + username + "\n");
        resultBuilder.append("Уровень доступа: " + accessLevel.getName() + "\n");
        if (status.isAdmin())
            resultBuilder.append("*Администратор*");

        return resultBuilder.toString();
    }
}

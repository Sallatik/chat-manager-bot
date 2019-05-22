package lat.sal.zwolabot.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Chat {

    @Id
    private long id;
    private String title;
    private String description;
    private String inviteLink;

    @ManyToOne
    private AccessLevel accessLevel;

    @OneToMany(mappedBy = "chat")
    private List<ChatUser> users;

    public void update(Chat chat) {

        setTitle(chat.getTitle());
        setDescription(chat.getDescription());
        setInviteLink(chat.getInviteLink());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInviteLink() {
        return inviteLink;
    }

    public void setInviteLink(String inviteLink) {
        this.inviteLink = inviteLink;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public List<ChatUser> getUsers() {
        return users;
    }

    public void setUsers(List<ChatUser> users) {
        this.users = users;
    }

    public Chat(com.pengrad.telegrambot.model.Chat chat) {

        this.id = chat.id();
        this.title = chat.title();
        this.description = chat.description();
        this.inviteLink = chat.inviteLink();
    }

    public Chat() {}
}

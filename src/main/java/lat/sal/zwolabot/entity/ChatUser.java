package lat.sal.zwolabot.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.util.Date;

@Entity
public class ChatUser {

    @EmbeddedId
    private ChatUserId chatUserId;


    @MapsId("chatId")
    @ManyToOne
    private Chat chat;

    @MapsId("userId")
    @ManyToOne
    private User user;

    private int warns;
    private Date lastMessageDate;
    private boolean banned;
    private String note;

    public ChatUserId getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(ChatUserId chatUserId) {
        this.chatUserId = chatUserId;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getWarns() {
        return warns;
    }

    public void setWarns(int warns) {
        this.warns = warns;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ChatUser() {}
}

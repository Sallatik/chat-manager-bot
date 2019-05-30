package lat.sal.zwolabot.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.text.SimpleDateFormat;
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
    private long lastMessage;
    private boolean banned;
    private boolean moderator;
    private String note;

    public boolean isModerator() {
        return moderator;
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
    }

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

    public long getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(long lastMessage) {
        this.lastMessage = lastMessage;
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

    public ChatUser(long chatId, long userId) {

        this.chatUserId = new ChatUserId(chatId, userId);
    }

    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String result = "*Пользователь:* _" + chatUserId.getUserId() + "_\n" +
                "Предупреждений: " + warns + "\n" +
                "Последнее сообщение: " + (lastMessage != 0 ? format.format(new Date(lastMessage * 1000)) : "никогда") + "\n";

        if (banned) {
            result += "*Забанен* ";
            if (!note.equals(""))
                result += "по причине _" + note + "_";
        }

        return result;
    }

    public ChatUser(Chat chat, User user) {

        this.chatUserId = new ChatUserId(chat.getId(), user.getId());
        this.chat = chat;
        this.user = user;
    }
}

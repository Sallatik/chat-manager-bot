package lat.sal.zwolabot.entity;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChatUserId implements Serializable {

    private long userId;
    private long chatId;


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatUserId)) return false;
        ChatUserId that = (ChatUserId) o;
        return userId == that.userId &&
                chatId == that.chatId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, chatId);
    }

    public ChatUserId () {}

    public ChatUserId(long chatId, long userId) {

        this.chatId = chatId;
        this.userId = userId;
    }
}

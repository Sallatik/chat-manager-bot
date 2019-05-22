package lat.sal.zwolabot.service;

import lat.sal.zwolabot.entity.AccessLevel;
import lat.sal.zwolabot.entity.Chat;

import java.util.List;

public class LevelAndChats {

    private AccessLevel level;
    private List<Chat> chats;

    public AccessLevel getLevel() {
        return level;
    }

    public void setLevel(AccessLevel level) {
        this.level = level;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public LevelAndChats(AccessLevel level, List<Chat> chats) {
        this.level = level;
        this.chats = chats;
    }
}

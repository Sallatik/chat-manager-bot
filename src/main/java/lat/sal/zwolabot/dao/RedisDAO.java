package lat.sal.zwolabot.dao;

import java.util.Set;

public interface RedisDAO {

    Set<String> getRestrictedWords();

    Set<String> getRestrictedStickers();

    Set<String> getRestrictedPacks();

    void addRestrictedWord(String word);

    void addRestrictedSticker(String stickerId);

    void addRestrictedPack(String packName);

    void removeRestrictedWord(String word);

    void removeRestrictedSticker(String stickerId);

    void removeRestrictedPack(String packName);

    boolean isRegistrationOpen();

    void setRegistrationOpen(boolean registrationOpen);
}

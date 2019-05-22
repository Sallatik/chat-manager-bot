package lat.sal.zwolabot.service;

import java.util.List;
import java.util.Set;

public interface FilterService {

    void onTextMessage(String text, long chatId, long userId, int messageId);

    void onStickerMessage(String setName, String fileId, long chatId, long userId, int messageId);

    void restrictWord(String word);

    void unrestrictWord(String word);

    void restrictSticker(String stickerId);

    void restrictPack(String packName);

    Set<String> getRestrictedWords();
}

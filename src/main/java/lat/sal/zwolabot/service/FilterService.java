package lat.sal.zwolabot.service;

public interface FilterService {

    void onTextMessage(String text, long chatId, long userId, long messageId);

    void onStickerMessage(String setName, String fileId, long chatId, long userId, long messageId);

}

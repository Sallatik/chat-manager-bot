package lat.sal.zwolabot.service;

import org.springframework.stereotype.Component;

@Component
public class FilterServiceImpl implements FilterService {

    @Override
    public void onTextMessage(String text, long chatId, long userId, long messageId) {

    }

    @Override
    public void onStickerMessage(String setName, String fileId, long chatId, long userId, long messageId) {

    }
}

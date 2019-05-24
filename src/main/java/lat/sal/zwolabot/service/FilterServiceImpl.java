package lat.sal.zwolabot.service;

import lat.sal.zwolabot.dao.RedisDAO;
import lat.sal.zwolabot.telegram.TelegramFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Service
public class FilterServiceImpl implements FilterService {

    private TelegramFacade telegramFacade;
    private RedisDAO redisDAO;

    @Override
    public void onTextMessage(String text, long chatId, long userId, int messageId) {

        text = text.toLowerCase();
        boolean illegal = false;
        for (String word : redisDAO.getRestrictedWords()) {

            if (text.contains(word)) {
                illegal = true;
                text = text.replace(word, "%жидовское слово%");
            }
        }

        if (illegal) {

            telegramFacade.deleteMessage(chatId, messageId);
            telegramFacade.sendMessage("Удалено сообщение: \n\"" + text + "\"", chatId);
        }

    }

    @Override
    public void onStickerMessage(String setName, String fileId, long chatId, long userId, int messageId) {

        if (redisDAO.getRestrictedPacks().contains(setName) || redisDAO.getRestrictedStickers().contains(fileId))
            telegramFacade.deleteMessage(chatId, messageId);
    }

    @Override
    public void restrictWord(String word) {

        redisDAO.addRestrictedWord(word);
    }

    @Override
    public void restrictSticker(String stickerId) {

        redisDAO.addRestrictedSticker(stickerId);
    }

    @Override
    public void restrictPack(String packName) {

        redisDAO.addRestrictedPack(packName);
    }

    @Override
    public Set<String> getRestrictedWords() {

        return new HashSet<>(redisDAO.getRestrictedWords());
    }

    @Override
    public Set<String> getRestrictedPacks() {

        return new HashSet<>(redisDAO.getRestrictedPacks());
    }

    @Override
    public Set<String> getRestrictedStickers() {

        return new HashSet<>(redisDAO.getRestrictedStickers());
    }

    @Override
    public void unrestrictWord(String word) {

        redisDAO.removeRestrictedWord(word.toLowerCase());
    }

    @Override
    public void unrestrictSticker(String stickerId) {

        redisDAO.removeRestrictedSticker(stickerId);
    }

    @Override
    public void unrestrictPack(String packName) {

        redisDAO.removeRestrictedPack(packName);
    }

    @Autowired
    public FilterServiceImpl(TelegramFacade telegramFacade, RedisDAO redisDAO) {

        this.telegramFacade = telegramFacade;
        this.redisDAO = redisDAO;
    }
}

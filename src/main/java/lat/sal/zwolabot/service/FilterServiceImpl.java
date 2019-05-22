package lat.sal.zwolabot.service;

import lat.sal.zwolabot.telegram.TelegramFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FilterServiceImpl implements FilterService {

    private TelegramFacade telegramFacade;
    private Jedis jedis;

    private Set<String> words;
    private Set<String> stickers;
    private Set<String> packs;

    @Override
    public void onTextMessage(String text, long chatId, long userId, int messageId) {

        text = text.toLowerCase();
        boolean illegal = false;
        for (String word : words) {

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

    }

    @PostConstruct
    public void init() {

        words = jedis.smembers("words");
        stickers = jedis.smembers("stikers");
        packs = jedis.smembers("packs");
    }

    @Override
    public void restrictWord(String word) {

        jedis.sadd("words", word.toLowerCase());
        words = jedis.smembers("words");
    }

    @Override
    public void restrictSticker(String stickerId) {

        jedis.sadd("stickers", stickerId);
        stickers = jedis.smembers("stickers");
    }

    @Override
    public void restrictPack(String packName) {


        jedis.sadd("packs", packName);
        packs = jedis.smembers("packs");
    }

    @Override
    public Set<String> getRestrictedWords() {

        return new HashSet<>(words);
    }

    @Override
    public void unrestrictWord(String word) {

        jedis.srem("words", word.toLowerCase());
        words = jedis.smembers("words");
    }

    @Autowired
    public FilterServiceImpl(TelegramFacade telegramFacade, Jedis jedis) {
        this.telegramFacade = telegramFacade;
        this.jedis = jedis;
    }
}

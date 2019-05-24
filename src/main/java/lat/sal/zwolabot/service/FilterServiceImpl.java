package lat.sal.zwolabot.service;

import lat.sal.zwolabot.telegram.TelegramFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class FilterServiceImpl implements FilterService {

    private TelegramFacade telegramFacade;
    private Jedis jedis;

    @Value("${zwolabot.wordfilter.words-key:words}")
    private String wordsKey;
    @Value("${zwolabot.wordfilter.stickers-key:stickers}")
    private String stickersKey;
    @Value("${zwolabot.wordfilter.packs-key:packs}")
    private String packsKey;

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

        if (packs.contains(setName) || stickers.contains(fileId))
            telegramFacade.deleteMessage(chatId, messageId);
    }

    @PostConstruct
    public void init() {

        words = jedis.smembers(wordsKey);
        stickers = jedis.smembers(stickersKey);
        packs = jedis.smembers(packsKey);
    }

    @Override
    public void restrictWord(String word) {

        jedis.sadd(wordsKey, word.toLowerCase());
        words = jedis.smembers(wordsKey);
    }

    @Override
    public void restrictSticker(String stickerId) {

        jedis.sadd(stickersKey, stickerId);
        stickers = jedis.smembers(stickersKey);
    }

    @Override
    public void restrictPack(String packName) {


        jedis.sadd(packsKey, packName);
        packs = jedis.smembers(packsKey);
    }

    @Override
    public Set<String> getRestrictedWords() {

        return new HashSet<>(words);
    }

    @Override
    public Set<String> getRestrictedPacks() {

        return new HashSet<>(packs);
    }

    @Override
    public Set<String> getRestrictedStickers() {

        return new HashSet<>(stickers);
    }

    @Override
    public void unrestrictWord(String word) {

        jedis.srem(wordsKey, word.toLowerCase());
        words = jedis.smembers(wordsKey);
    }

    @Override
    public void unrestrictSticker(String stickerId) {

        jedis.srem(stickersKey, stickerId);
        stickers = jedis.smembers(stickersKey);
    }

    @Override
    public void unrestrictPack(String packName) {

        jedis.srem(packsKey, packName);
        packs = jedis.smembers(packsKey);
    }

    @Autowired
    public FilterServiceImpl(TelegramFacade telegramFacade, Jedis jedis) {

        this.telegramFacade = telegramFacade;
        this.jedis = jedis;
    }
}

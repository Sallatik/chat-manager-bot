package lat.sal.zwolabot.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.Set;

@Repository
public class FilterDAOImpl implements FilterDAO {

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
    public Set<String> getRestrictedWords() {
        return words;
    }

    @Override
    public Set<String> getRestrictedStickers() {
        return stickers;
    }

    @Override
    public Set<String> getRestrictedPacks() {
        return packs;
    }

    @Override
    public void addRestrictedWord(String word) {

        jedis.sadd(wordsKey, word.toLowerCase());
        words = jedis.smembers(wordsKey);
    }

    @Override
    public void addRestrictedSticker(String stickerId) {

        jedis.sadd(stickersKey, stickerId);
        stickers = jedis.smembers(stickersKey);
    }

    @Override
    public void addRestrictedPack(String packName) {

        jedis.sadd(packsKey, packName);
        packs = jedis.smembers(packsKey);
    }

    @Override
    public void removeRestrictedWord(String word) {

        jedis.srem(wordsKey, word.toLowerCase());
        words = jedis.smembers(wordsKey);
    }

    @Override
    public void removeRestrictedSticker(String stickerId) {

        jedis.srem(stickersKey, stickerId);
        stickers = jedis.smembers(stickersKey);
    }

    @Override
    public void removeRestrictedPack(String packName) {

        jedis.srem(packsKey, packName);
        packs = jedis.smembers(packsKey);
    }

    @PostConstruct
    public void init() {

        words = jedis.smembers(wordsKey);
        stickers = jedis.smembers(stickersKey);
        packs = jedis.smembers(packsKey);
    }

    @Autowired
    public FilterDAOImpl(Jedis jedis) {
        this.jedis = jedis;
    }
}

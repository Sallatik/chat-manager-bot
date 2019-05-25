package lat.sal.zwolabot.service;

import lat.sal.zwolabot.dao.FilterDAO;
import lat.sal.zwolabot.telegram.TelegramFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class FilterServiceImpl implements FilterService {

    private TelegramFacade telegramFacade;
    private FilterDAO filterDAO;

    @Override
    public void onTextMessage(String text, long chatId, long userId, int messageId) {

        text = text.toLowerCase();
        boolean illegal = false;
        for (String word : filterDAO.getRestrictedWords()) {

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

        if (filterDAO.getRestrictedPacks().contains(setName) || filterDAO.getRestrictedStickers().contains(fileId))
            telegramFacade.deleteMessage(chatId, messageId);
    }

    @Override
    public void restrictWord(String word) {

        filterDAO.addRestrictedWord(word);
    }

    @Override
    public void restrictSticker(String stickerId) {

        filterDAO.addRestrictedSticker(stickerId);
    }

    @Override
    public void restrictPack(String packName) {

        filterDAO.addRestrictedPack(packName);
    }

    @Override
    public Set<String> getRestrictedWords() {

        return new HashSet<>(filterDAO.getRestrictedWords());
    }

    @Override
    public Set<String> getRestrictedPacks() {

        return new HashSet<>(filterDAO.getRestrictedPacks());
    }

    @Override
    public Set<String> getRestrictedStickers() {

        return new HashSet<>(filterDAO.getRestrictedStickers());
    }

    @Override
    public void unrestrictWord(String word) {

        filterDAO.removeRestrictedWord(word.toLowerCase());
    }

    @Override
    public void unrestrictSticker(String stickerId) {

        filterDAO.removeRestrictedSticker(stickerId);
    }

    @Override
    public void unrestrictPack(String packName) {

        filterDAO.removeRestrictedPack(packName);
    }

    @Autowired
    public FilterServiceImpl(TelegramFacade telegramFacade, FilterDAO filterDAO) {

        this.telegramFacade = telegramFacade;
        this.filterDAO = filterDAO;
    }
}

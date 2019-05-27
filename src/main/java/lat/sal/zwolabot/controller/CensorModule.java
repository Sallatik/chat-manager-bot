package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Sticker;
import lat.sal.zwolabot.ZwolabotException;
import lat.sal.zwolabot.controller.annotation.Admin;
import lat.sal.zwolabot.controller.annotation.Respond;
import lat.sal.zwolabot.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sallat.jelebot.annotation.listeners.MessageListener;

@Component
public class CensorModule {

    private FilterService filterService;
    private ControllerHelper helper;

    @Admin
    @Respond
    @MessageListener(filter = "/filter & text & ! reply")
    public void restrictWord(Message message) {

        String word = helper.getArgument(message.text(), "/filter".length());
        filterService.restrictWord(word);
        String response = "Говорить '" + word + "' отныне запрещено во всех чатах системы";
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/unfilter & text & ! reply")
    public void unrestrictWord(Message message) {

        String word = helper.getArgument(message.text(), "/unfilter".length());
        filterService.unrestrictWord(word);
        String response = "Теперь можно говорить '" + word + "'!";
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/unfilter & reply")
    public void unrestrictSticker(Message message) {

        Sticker sticker = message.replyToMessage().sticker();

        if (sticker == null)
            throw new ZwolabotException("это не стикер");

        filterService.unrestrictSticker(sticker.fileId());
        String response = "Этот стикер больше не под запретом!";
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/packunfilter")
    public void unrestrictPack(Message message) {

        String packName;

        if (message.replyToMessage() != null) {

            Sticker sticker = message.replyToMessage().sticker();

            if (sticker == null)
                throw new ZwolabotException("это не стикер");

            packName = sticker.setName();

        } else {
            packName = helper.getArgument(message.text(), "/packunfilter".length());
        }

        filterService.unrestrictPack(packName);
        String response = "Стикерпак '" + helper.packLink(packName) + "' снова можно использовать!";
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/filter & reply")
    public void restrictSticker(Message message) {

        Sticker sticker = message.replyToMessage().sticker();

        if (sticker == null)
            throw new ZwolabotException("это не стикер");

        filterService.restrictSticker(sticker.fileId());
        String response = "Этот стикер теперь под запретом";
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/packfilter & reply")
    public void restrictPack(Message message) {

        Sticker sticker = message.replyToMessage().sticker();

        if (sticker == null)
            throw new ZwolabotException("это не стикер");

        filterService.restrictPack(sticker.setName());
        String response = "Стикерпак '" + helper.packLink(sticker.setName()) + "' теперь под запретом";
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/badwords")
    public void getRestrictedWords(Message message) {

        String response = "Фильтруемые слова и фразы: \n" + filterService.getRestrictedWords();
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/badpacks")
    public void getRestrictedPacks(Message message) {

        StringBuilder responseBuilder = new StringBuilder("Фильтруемые cтикер паки: \n");

        for (String packName : filterService.getRestrictedPacks())
            responseBuilder.append(helper.packLink(packName));
        String response = responseBuilder.toString();
        helper.reply(response, message);
    }

    @Admin
    @Respond
    @MessageListener(filter = "/badstickers")
    public void getRestrictedStickers(Message message) {

        for (String fileId : filterService.getRestrictedStickers())
            helper.stickerReply(fileId, message);
    }

    @MessageListener(filter = "supergroup & text & ! /filter & ! /unfilters")
    public void censorText(Message message) {
        if (message.from().isBot())
            return;

        filterService.onTextMessage(
                message.text(),
                message.chat().id(),
                message.from().id(),
                message.messageId()
        );
    }

    @MessageListener(filter = "supergroup & caption")
    public void censorCaption(Message message) {
        if (message.from().isBot())
            return;

        filterService.onTextMessage(
                message.caption(),
                message.chat().id(),
                message.from().id(),
                message.messageId()
        );
    }

    @MessageListener(filter = "supergroup & sticker")
    public void censorSticker(Message message) {

        filterService.onStickerMessage(
                message.sticker().setName(),
                message.sticker().fileId(),
                message.chat().id(),
                message.from().id(),
                message.messageId()
        );
    }

    @Autowired
    public CensorModule(FilterService filterService, ControllerHelper helper) {

        this.filterService = filterService;
        this.helper = helper;
    }
}

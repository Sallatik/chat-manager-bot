package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Sticker;
import com.pengrad.telegrambot.request.SendSticker;
import lat.sal.zwolabot.ZwolabotApplication;
import lat.sal.zwolabot.ZwolabotException;
import lat.sal.zwolabot.service.ErrorService;
import lat.sal.zwolabot.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sallat.jelebot.annotation.listeners.MessageListener;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CensorModule {

    private ErrorService errorService;
    private FilterService filterService;
    private ControllerHelper helper;

    @MessageListener(filter = "/filter & text & ! reply")
    public void restrictWord(Message message) {

        try {

            errorService.requireAdmin(message.from().id());
            String word = helper.getArgument(message.text(), "/filter".length());
            filterService.restrictWord(word);
            helper.reply("Готово", message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/unfilter & text & ! reply")
    public void unrestrictWord(Message message) {

        try {

            errorService.requireAdmin(message.from().id());
            String word = helper.getArgument(message.text(), "/unfilter".length());
            filterService.unrestrictWord(word);
            helper.reply("Готово", message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/unfilter & reply")
    public void unrestrictSticker(Message message) {

        try {

            errorService.requireAdmin(message.from().id());
            Sticker sticker = message.replyToMessage().sticker();

            if (sticker == null)
                throw new ZwolabotException("это не стикер");

            filterService.unrestrictSticker(sticker.fileId());
            helper.reply("Готово", message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/packunfilter")
    public void unrestrictPack(Message message) {

        try {

            errorService.requireAdmin(message.from().id());

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
            helper.reply("Готово", message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/filter & reply")
    public void restrictSticker(Message message) {

        try {

            errorService.requireAdmin(message.from().id());
            Sticker sticker = message.replyToMessage().sticker();

            if (sticker == null)
                throw new ZwolabotException("это не стикер");

            filterService.restrictSticker(sticker.fileId());
            helper.reply("Готово", message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/packfilter & reply")
    public void restrictPack(Message message) {

        try {

            errorService.requireAdmin(message.from().id());
            Sticker sticker = message.replyToMessage().sticker();

            if (sticker == null)
                throw new ZwolabotException("это не стикер");

            filterService.restrictPack(sticker.setName());
            helper.reply("Готово", message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }
    }

    @MessageListener(filter = "/badwords")
    public void getRestrictedWords(Message message) {

        try {

            errorService.requireAdmin(message.from().id());
            helper.reply("Фильтруемые слова и фразы: \n" + filterService.getRestrictedWords(), message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }

    }

    @MessageListener(filter = "/badpacks")
    public void getRestrictedPacks(Message message) {

        try {

            errorService.requireAdmin(message.from().id());

            StringBuilder response = new StringBuilder("Фильтруемые cтикер паки: \n");

            for (String packName : filterService.getRestrictedPacks()) {
                response.append("[" + packName + "](https://t.me/addstickers/" + packName + ") ");
            }

            helper.reply(response.toString(), message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }

    }

    @MessageListener(filter = "/badstickers")
    public void getRestrictedStickers(Message message) {

        try {

            errorService.requireAdmin(message.from().id());

            for (String fileId : filterService.getRestrictedStickers())
                helper.stickerReply(fileId, message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }

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
    public CensorModule(ErrorService errorService, FilterService filterService, ControllerHelper helper) {
        this.errorService = errorService;
        this.filterService = filterService;
        this.helper = helper;
    }
}

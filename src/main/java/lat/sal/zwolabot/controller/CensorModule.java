package lat.sal.zwolabot.controller;

import com.pengrad.telegrambot.model.Message;
import lat.sal.zwolabot.ZwolabotException;
import lat.sal.zwolabot.service.ErrorService;
import lat.sal.zwolabot.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sallat.jelebot.annotation.listeners.MessageListener;

@Component
public class CensorModule {

    private ErrorService errorService;
    private FilterService filterService;
    private ControllerHelper helper;

    @MessageListener(filter = "/filter & text & private")
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

    @MessageListener(filter = "/unfilter & text & private")
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

    @MessageListener(filter = "/badwords & private")
    public void getRestrictedWords(Message message) {

        try {

            errorService.requireAdmin(message.from().id());
            helper.reply("Фильтруемые слова и фразы: \n" + filterService.getRestrictedWords(), message);

        } catch (ZwolabotException e) {
            helper.reply(e.getMessage(), message);
        }

    }

    @MessageListener(filter = "supergroup & text")
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

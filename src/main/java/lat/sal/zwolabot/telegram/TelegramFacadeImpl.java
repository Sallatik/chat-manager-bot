package lat.sal.zwolabot.telegram;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.GetChatResponse;
import com.pengrad.telegrambot.response.StringResponse;
import lat.sal.zwolabot.ZwolabotException;
import lat.sal.zwolabot.entity.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TelegramFacadeImpl implements TelegramFacade{

    private TgSender tgSender;

    @Override
    public void kick(long chatId, long userId) {

        ban(chatId, userId);
        unban(chatId, userId);
    }

    @Override
    public void ban(long chatId, long userId) {

        KickChatMember kickChatMember = new KickChatMember(chatId, (int) userId);
        if(!tgSender.executeOrLog(kickChatMember).isOk())
            throw new ZwolabotException("Не удалось забанить пользователя.");
    }

    @Override
    public void unban(long chatId, long userId) {

        UnbanChatMember unbanChatMember = new UnbanChatMember(chatId, (int) userId);
        if(!tgSender.executeOrLog(unbanChatMember).isOk())
            throw new ZwolabotException("Не удалось разбанить пользователя.");
    }

    @Override
    public Chat getChat(long id) {

        StringResponse stringResponse = tgSender.executeOrLog(new ExportChatInviteLink(id));
        GetChatResponse getChatResponse = tgSender.executeOrLog(new GetChat(id));

        if (!(stringResponse.isOk() && getChatResponse.isOk()))
            throw new ZwolabotException("Не удалось получить данные чата.");

        return new Chat(getChatResponse.chat());
    }

    @Override
    public void deleteMessage(long chatId, int messageId) {

        tgSender.executeOrLog(new DeleteMessage(chatId, messageId));
    }

    @Override
    public void sendMessage(String text, long chatId) {

        tgSender.executeOrLog(new SendMessage(chatId, text).parseMode(ParseMode.Markdown).disableWebPagePreview(true    ));
    }

    @Autowired
    public TelegramFacadeImpl(TgSender tgSender) {
        this.tgSender = tgSender;
    }
}

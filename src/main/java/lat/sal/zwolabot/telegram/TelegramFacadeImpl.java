package lat.sal.zwolabot.telegram;

import com.pengrad.telegrambot.request.KickChatMember;
import com.pengrad.telegrambot.request.UnbanChatMember;
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
        tgSender.executeOrLog(kickChatMember);
    }

    @Override
    public void unban(long chatId, long userId) {

        UnbanChatMember unbanChatMember = new UnbanChatMember(chatId, (int) userId);
        tgSender.executeOrLog(unbanChatMember);
    }

    @Autowired
    public TelegramFacadeImpl(TgSender tgSender) {
        this.tgSender = tgSender;
    }
}

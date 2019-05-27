package lat.sal.zwolabot.controller.aspect;

import com.pengrad.telegrambot.model.Message;
import lat.sal.zwolabot.ZwolabotException;
import lat.sal.zwolabot.service.ChatService;
import lat.sal.zwolabot.service.UserService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class SecurityAspect {

    private UserService userService;
    private ChatService chatService;

    @Before("@annotation(lat.sal.zwolabot.controller.annotation.Root) && args(message)")
    public void requireRoot(Message message) {

        if (!userService.isRoot(message.from().id()))
            throw new ZwolabotException("Вы не главный админ!");
    }

    @Before("@annotation(lat.sal.zwolabot.controller.annotation.Admin) && args(message)")
    public void requireAdmin(Message message) {

        if (!userService.isAdmin(message.from().id()))
            throw new ZwolabotException("Вы не админ!");
    }

    @Before("@annotation(lat.sal.zwolabot.controller.annotation.Moderator) && args(message)")
    public void requireModerator(Message message) {

        if (!userService.isAdmin(message.from().id()) && !chatService.isModerator(message.chat().id(), message.from().id()))
            throw new ZwolabotException("Вы не модератор!");
    }

    @Autowired
    public SecurityAspect(UserService userService, ChatService chatService) {

        this.userService = userService;
        this.chatService = chatService;
    }
}

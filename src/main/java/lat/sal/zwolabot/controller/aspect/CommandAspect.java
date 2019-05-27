package lat.sal.zwolabot.controller.aspect;

import com.pengrad.telegrambot.model.Message;
import lat.sal.zwolabot.ZwolabotException;
import lat.sal.zwolabot.controller.ControllerHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Aspect
@Component
@Order(0)
public class CommandAspect {

    private ControllerHelper helper;
    private Logger logger = Logger.getLogger(CommandAspect.class.getName());

    @Around(value = "@annotation(lat.sal.zwolabot.controller.annotation.Respond) && " +
                    "args(message)")
    public void respond(ProceedingJoinPoint joinPoint, Message message) throws Throwable {

        try {

            joinPoint.proceed();
        } catch (ZwolabotException e) {

            helper.reply(e.getMessage(), message);
        } catch (RuntimeException e) {

            logger.log(Level.WARNING, "Error processing message '" + message.text() + "'" + e);
            helper.reply("Неизвестная ошибка", message);
        }
    }


    @Autowired
    public CommandAspect(ControllerHelper helper) {
        this.helper = helper;
    }
}

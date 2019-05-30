package lat.sal.zwolabot.controller;

import lat.sal.zwolabot.service.UserService;
import lat.sal.zwolabot.telegram.TgSender;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ControllerHelperTest {

    @Test
    void test() {

        ControllerHelper helper =
                new ControllerHelper(Mockito.mock(TgSender.class), Mockito.mock(UserService.class));

        String single = "/ban po prichine pidoras";
        String two = "/warn @sallatik po prichine pidoras";

        assertEquals("po prichine pidoras", helper.getSingleArg(single));
        assertEquals("po prichine pidoras", helper.getSecondArg(two));
    }

}
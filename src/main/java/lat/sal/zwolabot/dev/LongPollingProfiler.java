package lat.sal.zwolabot.dev;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import sallat.jelebot.update.UpdateListener;
import sallat.jelebot.update.UpdateSource;

import java.util.List;
import java.util.logging.Logger;

@Component
public class LongPollingProfiler implements UpdateSource {

    private Logger logger = Logger.getLogger(LongPollingProfiler.class.getName());

    private long min = Long.MAX_VALUE;
    private long max = Long.MIN_VALUE;
    private long avg = 0;

    private UpdateListener updateListener;

    @Override
    public void startGettingUpdates(UpdateListener updateListener, TelegramBot telegramBot) {

        this.updateListener = updateListener;
        telegramBot.setUpdatesListener(this::process);
    }

    private int process(List<Update> updates) {

        for (Update update : updates) {

            long start = System.nanoTime();
            updateListener.onUpdate(update);
            long end = System.nanoTime();

            long time = end - start;

            if (time < min)
                min = time;
            if (time > max)
                max = time;
            if (avg == 0)
                avg = time;
            else
                avg = (time + avg) / 2;

            logger.info("time: " + time + ", min: " + min + ", max: " + max + ", avg: " + avg);
        }

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}

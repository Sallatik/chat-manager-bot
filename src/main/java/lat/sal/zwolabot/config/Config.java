package lat.sal.zwolabot.config;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sallat.jelebot.Jelebot;
import sallat.jelebot.update.LongPollingUpdateSource;
import sallat.jelebot.update.UpdateSource;

@Configuration
public class Config {

    @Bean
    public UpdateSource updateSource() {

        return new LongPollingUpdateSource();
    }

    @Bean
    public TelegramBot bot() {

        return new TelegramBot("611975675:AAGGPe6UyjpMVlll2yyHEyA_BNafAtHVnb8");
    }

    @Bean
    public Jelebot jelebot() {

        return Jelebot.create(bot())
                // register modules
                .setUpdateSource(updateSource());
    }
}

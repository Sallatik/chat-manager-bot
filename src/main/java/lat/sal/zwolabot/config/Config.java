package lat.sal.zwolabot.config;

import com.pengrad.telegrambot.TelegramBot;
import lat.sal.zwolabot.controller.AutoModule;
import lat.sal.zwolabot.controller.CommandModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import sallat.jelebot.Jelebot;
import sallat.jelebot.update.LongPollingUpdateSource;
import sallat.jelebot.update.UpdateSource;

@Configuration
public class Config {

    @Autowired
    private ApplicationContext context;

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
                .register(context.getBean(AutoModule.class))
                .register(context.getBean(CommandModule.class))
                .setUpdateSource(updateSource());
    }

    @Bean
    public Jedis jedis() {
        return new Jedis("localhost");
    }
}

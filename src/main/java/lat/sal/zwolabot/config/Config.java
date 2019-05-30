package lat.sal.zwolabot.config;

import com.pengrad.telegrambot.TelegramBot;
import lat.sal.zwolabot.controller.annotation.Admin;
import lat.sal.zwolabot.dev.LongPollingProfiler;
import lat.sal.zwolabot.controller.ButlerModule;
import lat.sal.zwolabot.controller.BaseModule;
import lat.sal.zwolabot.controller.CensorModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import redis.clients.jedis.Jedis;
import sallat.jelebot.Jelebot;
import sallat.jelebot.update.HttpWebhookUpdateSource;
import sallat.jelebot.update.LongPollingUpdateSource;
import sallat.jelebot.update.UpdateSource;

import java.net.InetSocketAddress;

@Configuration
public class Config {

    @Value("${zwolabot.heroku}")
    private boolean heroku;

    @Autowired
    private ApplicationContext context;
    @Autowired
    private Environment env;

    @Bean
    public UpdateSource updateSource() {

        if (heroku) {
            String url = env.getProperty("zwolabot.heroku.app-url");
            int port = Integer.parseInt(System.getenv("PORT"));
            return new HttpWebhookUpdateSource(url, new InetSocketAddress(port));
        } else
            return new LongPollingUpdateSource();
    }

    @Bean
    public TelegramBot bot() {

        return new TelegramBot(env.getProperty("zwolabot.token"));
    }

    @Bean
    public Jelebot jelebot() {

        return Jelebot.create(bot())
                // register modules
                .register(context.getBean(BaseModule.class))
                .register(context.getBean(ButlerModule.class))
                .register(context.getBean(CensorModule.class))
                .setUpdateSource(updateSource());
                //.setUpdateSource(context.getBean(LongPollingProfiler.class));
    }

    @Bean
    public Jedis jedis() {
        return new Jedis(env.getProperty("zwolabot.redis-url"));
    }
}

package lat.sal.zwolabot;

import lat.sal.zwolabot.config.DBInitializerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sallat.jelebot.Jelebot;

import java.util.logging.Logger;

@SpringBootApplication
public class ZwolabotApplication implements CommandLineRunner {

	private DBInitializerBean dbInitializerBean;
	private Jelebot jelebot;
	private Logger logger = Logger.getLogger(ZwolabotApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(ZwolabotApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		logger.info("Initializing DB");
		dbInitializerBean.init();
		logger.info("Starting telegram bot");
		jelebot.start();
		logger.info("Successfully started!");
	}

	@Autowired
	public ZwolabotApplication(DBInitializerBean dbInitializerBean, Jelebot jelebot) {
		this.dbInitializerBean = dbInitializerBean;
		this.jelebot = jelebot;
	}
}

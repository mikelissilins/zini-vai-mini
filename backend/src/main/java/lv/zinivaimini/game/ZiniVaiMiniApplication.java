package lv.zinivaimini.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ZiniVaiMiniApplication {

	public static void main(String[] args) {
		String databaseUrl = System.getenv("DATABASE_URL");
		if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
			System.setProperty("spring.datasource.url", "jdbc:" + databaseUrl);
		}
		SpringApplication.run(ZiniVaiMiniApplication.class, args);
	}

}

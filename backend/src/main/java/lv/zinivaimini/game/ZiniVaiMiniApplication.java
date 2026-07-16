package lv.zinivaimini.game;

import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ZiniVaiMiniApplication {

	public static void main(String[] args) {
		String databaseUrl = System.getenv("DATABASE_URL");
		if (databaseUrl != null) {
			System.setProperty("spring.datasource.url", normalizeDatabaseUrl(databaseUrl));
		}
		SpringApplication.run(ZiniVaiMiniApplication.class, args);
	}

	static String normalizeDatabaseUrl(String databaseUrl) {
		if (!databaseUrl.startsWith("postgresql://")) {
			return databaseUrl;
		}

		URI uri = URI.create(databaseUrl);
		String port = uri.getPort() < 0 ? "" : ":" + uri.getPort();
		String query = uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery();
		return "jdbc:postgresql://" + uri.getHost() + port + uri.getRawPath() + query;
	}

}

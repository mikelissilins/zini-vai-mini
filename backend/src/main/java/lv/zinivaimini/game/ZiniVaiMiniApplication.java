package lv.zinivaimini.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ZiniVaiMiniApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZiniVaiMiniApplication.class, args);
	}

}

package living.word.livingword;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LivingWordApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivingWordApplication.class, args);
	}

}

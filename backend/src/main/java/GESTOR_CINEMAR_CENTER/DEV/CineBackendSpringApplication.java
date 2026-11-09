package GESTOR_CINEMAR_CENTER.DEV;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CineBackendSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CineBackendSpringApplication.class, args);
	}

}

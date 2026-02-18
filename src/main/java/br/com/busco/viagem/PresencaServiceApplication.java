package br.com.busco.viagem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;
import org.springframework.scheduling.annotation.EnableAsync;

@Modulith
@EnableAsync
@SpringBootApplication
public class PresencaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PresencaServiceApplication.class, args);
	}

}

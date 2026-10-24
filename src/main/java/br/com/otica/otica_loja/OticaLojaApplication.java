package br.com.otica.otica_loja;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class OticaLojaApplication {
	@PostConstruct
	public void init() {
		// Define o TimeZone padrão da JVM para o Brasil
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
	}

	public static void main(String[] args) {
		SpringApplication.run(OticaLojaApplication.class, args);
	}


}

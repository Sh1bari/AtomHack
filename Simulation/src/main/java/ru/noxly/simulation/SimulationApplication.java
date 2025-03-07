package ru.noxly.simulation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaRepositories
@SpringBootApplication(scanBasePackages = {
		"ru.noxly.simulation",    // Пакет вашего приложения"
		"ru.sh1bari.resolver",    // Пакет библиотеки
		"ru.noxly.validation",    // Пакет библиотеки
})
public class SimulationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimulationApplication.class, args);
	}

}

package it.thesis.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringbootApplication {
    static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}
}
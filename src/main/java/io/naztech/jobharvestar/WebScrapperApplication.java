package io.naztech.jobharvestar;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"io.naztech.jobharvestar"})
@EnableScheduling
@EnableAsync
@EnableJpaRepositories(basePackages = { "io.naztech.talent.dao" })
public class WebScrapperApplication extends SpringBootServletInitializer {

	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(WebScrapperApplication.class, args);
	}

}

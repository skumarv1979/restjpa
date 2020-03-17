package com.omrtb.restjpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.omrtb.restjpa.repository.CustomRepositoryImpl;

@ServletComponentScan
@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration
@EnableJpaRepositories (repositoryBaseClass = CustomRepositoryImpl.class)
public class RestjpaApplication extends ServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(RestjpaApplication.class, args);
	}

}

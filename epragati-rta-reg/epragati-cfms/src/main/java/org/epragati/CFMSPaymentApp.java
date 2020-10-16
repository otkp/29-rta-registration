package org.epragati;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
//@EnableScheduling
//@EnableMongoRepositories(basePackages= {"org.epragati.*"})
@EnableAutoConfiguration
@ComponentScan(basePackages={"org.epragati.*"})
public class CFMSPaymentApp extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(CFMSPaymentApp.class, args);
		System.out.println("The data is loaded...");
	}

	@Bean(name = "restTemplate")
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}



}

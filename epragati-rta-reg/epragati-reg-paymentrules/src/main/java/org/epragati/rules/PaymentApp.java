package org.epragati.rules;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

/*@ComponentScan(basePackages = { "org.epragati.*" })
@EntityScan({ "org.epragati.*" })*/
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@EnableMongoRepositories(basePackages= {"org.epragati.*"})
@ComponentScan(basePackages = { "org.epragati.*" })
@EntityScan({ "org.epragati.*" })
@SpringBootApplication
public class PaymentApp extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(PaymentApp.class, args);
	}

	@Bean
	public Gson gson() {
		return new Gson();
	}

	@Bean(name = "restTemplate")
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	

}
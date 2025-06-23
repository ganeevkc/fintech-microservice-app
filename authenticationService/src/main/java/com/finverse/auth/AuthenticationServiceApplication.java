package com.finverse.auth;

import com.finverse.auth.service.JWTTokenService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@Import({JWTTokenService.class})
@EnableDiscoveryClient
@EnableRabbit
public class AuthenticationServiceApplication implements CommandLineRunner{
	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {}
}

package com.finverse.auth;

import com.finverse.auth.service.JWTTokenService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@Import({JWTTokenService.class})
@EnableDiscoveryClient
@EnableRabbit
public class AuthenticationServiceApplication implements CommandLineRunner{
	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		PasswordEncoder encoder = new BCryptPasswordEncoder();
//
//		String rawPassword = "ganeevisthebest";
//		String hashedPassword = encoder.encode(rawPassword);
//
//		System.out.println("Raw password: " + rawPassword);
//		System.out.println("Hashed password: " + hashedPassword);
//
//		boolean match = encoder.matches(rawPassword, hashedPassword);
//		System.out.println("Match? " + match);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
//		textEncryptor.setPassword(PASSWORD_SALT);
//		String encryptedPassword = textEncryptor.encrypt(PASSWORD);
//
////		userRepository.save(new User("John", encryptedPassword));
////		userRepository.save(new User("Mindaugas", encryptedPassword));
//	}

}

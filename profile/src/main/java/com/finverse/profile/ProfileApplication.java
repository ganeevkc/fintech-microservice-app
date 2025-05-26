package com.finverse.profile;

import com.finverse.profile.domain.model.User;
import com.finverse.profile.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;

@SpringBootApplication
@EnableSwagger2
@Import({com.finverse.security.user.utils.JwtTokenUtil.class})
public class ProfileApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(ProfileApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		userRepository.save(
//				new User(
//						"John",
//						"John",
//						"Bongley",
//						31,
//						"Firefighter",
//						LocalDate.now()
//				)
//		);
	}
}

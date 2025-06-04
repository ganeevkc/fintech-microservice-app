package com.finverse.profile;

//import com.finverse.profile.domain.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
//@EnableSwagger2
@EnableFeignClients
@EnableDiscoveryClient
@Import({com.finverse.security.user.utils.JwtTokenUtil.class})
public class ProfileApplication implements CommandLineRunner {

//	@Autowired
//	private UserProfileRepository userProfileRepository;

	public static void main(String[] args) {
		SpringApplication.run(ProfileApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {

	}
}

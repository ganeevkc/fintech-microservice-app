package com.finverse.lendingengine;

import com.finverse.lendingengine.model.Balance;
import com.finverse.lendingengine.model.User;
import com.finverse.lendingengine.repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.stream.IntStream;

@SpringBootApplication
//@EnableSwagger2
@EnableDiscoveryClient
public class LendingengineApplication implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(LendingengineApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

//        userRepository.save(new User("Mindaugas", "Mindaugas", "Khandelwal", 25, "Software Developer", new Balance()));
//        userRepository.save(new User("John", "John", "Agrawal", 26, "Software Developer", new Balance()));

    }

//    @Bean
//    CommandLineRunner runner(UserRepository userRepository) { // Fake User Generator
//        Faker faker = new Faker();
//        return args -> IntStream.range(0, 10).forEach(i -> userRepository.save(new User(
//                faker.name().username(),
//                faker.name().firstName(),
//                faker.name().lastName(),
//                faker.number().numberBetween(20, 50),
//                faker.job().position(),
//                new Balance()
//        )));
//    }
}

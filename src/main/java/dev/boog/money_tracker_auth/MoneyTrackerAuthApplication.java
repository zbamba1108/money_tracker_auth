package dev.boog.money_tracker_auth;

import dev.boog.money_tracker_auth.entities.User;
import dev.boog.money_tracker_auth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class MoneyTrackerAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyTrackerAuthApplication.class, args);
    }

    @Autowired
    private UserRepository userRepository;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            List<User> userList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                User user = User.builder()
                        .email("user" + (i+1) + "@gmail.com")
                        .password("password")
                        .build();
                userList.add(user);
            }

            userRepository.saveAll(userList);
        };
    }
}

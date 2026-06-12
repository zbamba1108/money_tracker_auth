package dev.boog.money_tracker_auth;

import dev.boog.money_tracker_auth.entities.*;
import dev.boog.money_tracker_auth.repositories.*;
import java.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.*;

@SpringBootApplication
public class MoneyTrackerAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyTrackerAuthApplication.class, args);
    }

    /*@Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            List<User> userList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                User user = User.builder()
                        .email("user" + (i+1) + "@gmail.com")
                        .password(passwordEncoder.encode("password"))
                        .build();
                userList.add(user);
            }

            userRepository.saveAll(userList);
        };
    }*/
}

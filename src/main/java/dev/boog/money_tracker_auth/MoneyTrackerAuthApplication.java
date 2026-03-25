package dev.boog.money_tracker_auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;

@SpringBootApplication
public class MoneyTrackerAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyTrackerAuthApplication.class, args);
    }

}

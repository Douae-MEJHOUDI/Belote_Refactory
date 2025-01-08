package com.belote.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.belote.service.UserService;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) {
        // Create admin if it doesn't exist
        if (!userService.existsByUsername("admin")) {
            userService.createAdmin("admin", "admin123");
        }
    }
}

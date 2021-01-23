package de.mschoettle.boundary.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

@Configuration
public class SecurityUtilities {

    private static final int hashStrength = 15;

    private final static String salt = "2j9wqm2gma02mf0t392h30vm2";

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(hashStrength, new SecureRandom(salt.getBytes()));
    }
}

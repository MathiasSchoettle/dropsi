package de.mschoettle.control.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

@Configuration
public class SecurityUtilities {

    private static int hashStrength = 15;

    // TODO replace with other source
    private static String salt = "bitte ersetze mich";

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(hashStrength, new SecureRandom(salt.getBytes()));
    }
}

package de.mschoettle.control.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LoggingFactory {

    @Bean
    @Scope("singleton")
    public static Logger getControllerLogger() {
        return LoggerFactory.getLogger("Dropsi");
    }
}

package it.thesis.springboot.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {
    @Value("${logs.enabled}")
    private boolean enabled;

    @PostConstruct
    public void configureLogging() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        context.getLogger(Logger.ROOT_LOGGER_NAME)
                .setLevel(enabled ? Level.INFO : Level.ERROR);
    }
}
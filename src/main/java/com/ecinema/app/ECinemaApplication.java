package com.ecinema.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = "com.ecinema.app")
public class ECinemaApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Value("${MYSQL_HOST:default_val}")
    private String host;

    private final Logger logger = LoggerFactory.getLogger(ECinemaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ECinemaApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ECinemaApplication.class);
    }

    @Override
    public void run(String... args) {
        logger.debug("Host: {}", host);
    }

}

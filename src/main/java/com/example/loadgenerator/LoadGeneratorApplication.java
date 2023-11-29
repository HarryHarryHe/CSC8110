package com.example.loadgenerator;

import com.example.loadgenerator.service.LoadGenerator;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.HttpURLConnection;
import java.net.URL;

@SpringBootApplication
@EnableAsync

public class LoadGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoadGeneratorApplication.class, args);
    }

}

package com.turki.gamefyback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
@EnableCaching
public class GamefyBackApplication {

    public static void main(String[] args) {
                System.out.println("MONGODB_URI: " + System.getenv("MONGODB_URI"));
        SpringApplication.run(GamefyBackApplication.class, args);
    }
}
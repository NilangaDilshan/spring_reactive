package com.reactivespring.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class MoviesReviewServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoviesReviewServiceApplication.class, args);
    }
}
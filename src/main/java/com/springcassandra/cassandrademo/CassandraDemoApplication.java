package com.springcassandra.cassandrademo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CassandraDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CassandraDemoApplication.class, args);
    }
}

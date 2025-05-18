package com_apibancaria;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com_apibancaria")
@EnableJpaRepositories(basePackages = "com_apibancaria")
public class MainApplication {
    public static void main(String[] args) { SpringApplication.run(MainApplication.class, args);}

}

package com.lyselius.mandelbrotclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class MandelbrotclientApplication {




    @Bean
    public WebClient.Builder getWebClientBuilder() { return WebClient.builder(); }




    public static void main(String[] args) {
        SpringApplication.run(MandelbrotclientApplication.class, args);
    }

}

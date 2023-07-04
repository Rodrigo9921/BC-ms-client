package com.nttdata.msclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientConfig {
    @Value("${ms-transaccions.url}")
    private String msClientUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(msClientUrl)
                .build();
    }
}

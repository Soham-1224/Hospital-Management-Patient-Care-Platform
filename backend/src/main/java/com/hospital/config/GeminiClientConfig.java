package com.hospital.config;

import com.google.genai.Client;
import com.google.genai.types.HttpOptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiClientConfig {



    @Bean
    public Client googleGenAiClient() {

        HttpOptions httpOptions = HttpOptions.builder()
                .timeout(60_000)
                .build();

        return Client.builder()
                .httpOptions(httpOptions)
                .build();
    }
}

package com.example.movie_ticketing.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class WebClientConfig {

    @Bean
    fun tmdbWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://api.themoviedb.org/3")
            .build()
    }
}
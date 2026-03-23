package com.dev.assignment.api.controller.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.net.http.HttpClient
import java.time.Duration

@Configuration
class RestClientConfig {
    @Bean
    fun restClientBuilder(
        @Value($$"${evaluation.model-api.connect-timeout}") connectTimeout: Duration,
        @Value($$"${evaluation.model-api.read-timeout}") readTimeout: Duration
    ): RestClient.Builder {
        val httpClient = HttpClient.newBuilder()
            .connectTimeout(connectTimeout)
            .build()

        val requestFactory = JdkClientHttpRequestFactory(httpClient)
        requestFactory.setReadTimeout(readTimeout)

        return RestClient.builder()
            .requestFactory(requestFactory)
    }
}

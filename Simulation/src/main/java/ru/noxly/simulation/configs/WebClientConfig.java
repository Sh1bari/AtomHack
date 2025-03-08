package ru.noxly.simulation.configs;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static java.time.Duration.ofMillis;
import static ru.noxly.simulation.common.Constants.WEB_CLIENT_DYNDNS_BEAN;

@Configuration
public class WebClientConfig {

    @Value("${services.dyndns.url}")
    private String dyndnsBaseUrl;

    @Value("${services.dyndns.timeOut}")
    private Integer dyndnsTimeOut;

    @Bean(WEB_CLIENT_DYNDNS_BEAN)
    public WebClient dyndnsWebClient() {
        val httpClient = HttpClient.create().responseTimeout(ofMillis(dyndnsTimeOut));
        return WebClient
                .builder()
                .baseUrl(dyndnsBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
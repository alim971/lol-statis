package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@Slf4j
public class RiotApiHealthIndicator implements HealthIndicator {

    @Value("${API_KEY}")
    private String key;

    @Override
    public Health health() {
        URI uriHealth = UriComponentsBuilder.fromUriString("https://eun1.api.riotgames.com/lol/platform/v3/champion-rotations")
            .queryParam("api_key", key)
            .build().toUri();

        RestTemplate restTemplate = new RestTemplate();
        try {
            log.info("Trying to connect to: Riot API");
            restTemplate.getForObject(uriHealth, String.class);
            log.info("Riot API is up");

        } catch (HttpClientErrorException ex) {
            log.warn("Failed to connect to RIOT API");
            return Health.down()
                .withDetail("error", ex.getMessage())
                .build();
        }
        return Health.up().build();
    }

}

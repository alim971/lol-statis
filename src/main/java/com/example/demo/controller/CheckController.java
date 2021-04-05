package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@Slf4j

public class CheckController {
    @Value("${API_KEY}")
    private String key;

    @GetMapping("/health")
    public ResponseEntity checkHealth() {
        URI uriHealth = UriComponentsBuilder.fromUriString("https://eun1.api.riotgames.com/lol/platform/v3/champion-rotations")
            .queryParam("api_key", key)
            .build().toUri();

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.getForObject(uriHealth, String.class);

            return new ResponseEntity(HttpStatus.OK);
        } catch (HttpClientErrorException ex) {
            return new ResponseEntity(HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {

        log.info("Ping requested");
        return "OK";
    }
}

package com.example.demo.lol;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class CheckController {
    @Value("${API_KEY}")
    private String key;

    @GetMapping("/health")
    public ResponseEntity checkHealth() {
        final String uriHealth = "https://eun1.api.riotgames.com/lol/platform/v3/champion-rotations?api_key=RGAPI-852e62c4-eb53-4cd7-81a0-7617261f26a2" + key;

        RestTemplate restTemplate = new RestTemplate();
        try {
            String result = restTemplate.getForObject(uriHealth, String.class);

            return new ResponseEntity(HttpStatus.OK);
        } catch (HttpClientErrorException ex) {
            return new ResponseEntity(HttpStatus.FAILED_DEPENDENCY);
        }
    }

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "OK";
    }
}

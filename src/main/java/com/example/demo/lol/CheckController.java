package com.example.demo.lol;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckController {
    @GetMapping("/health")
    public ResponseEntity checkHealth() {
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "OK";
    }
}

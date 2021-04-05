package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchBean implements Serializable {
    private String queue;
    private String gameId;
    private String champion;
    private long timestamp;
    private String role;
    private String lane;
}

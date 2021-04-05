package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StatsBean implements Serializable {
    private String summonerName;
    private int team;
    private int games;
    private int streak;
    private boolean winStreak;
    private double winRate;
}

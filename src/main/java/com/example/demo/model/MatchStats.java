package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchStats implements Serializable {
    private String userName;
    private int userTeam;
    private boolean hasUserWon;
    private StatsBean[] players;
}

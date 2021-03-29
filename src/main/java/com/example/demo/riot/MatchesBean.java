package com.example.demo.riot;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchesBean implements Serializable {
    private int startIndex;
    private int endIndex;
    private int totalGames;
    private MatchBean[] matches;
}

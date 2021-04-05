package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserBean implements Serializable {
    private String id;
    private String accountId;
    private String currentAccountId;
    private String puuid;
    private String summonerName;
    private int profileIconId;
    private long revisionDate;
    private int summonerLevel;
}

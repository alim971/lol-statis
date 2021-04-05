package com.example.demo.service;

import com.example.demo.model.StatsBean;

public interface IStatsService {
    StatsBean getStatsForPlayer(String server, String userId, long timestamp, String summonerName, int team);
}

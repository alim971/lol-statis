package com.example.demo.service;

import com.example.demo.model.MatchDetailBean;
import com.example.demo.model.MatchesBean;
import org.springframework.stereotype.Service;

@Service
public interface IRiotService {
    String getAcccountId(String server, String username);

    MatchesBean getMatchHistory(String server, String id);

    MatchesBean getMatchHistory(String server, String id, int matches);

    MatchesBean getMatchHistory(String server, String id, int matches, long timestamp);

    MatchDetailBean getMatchDetail(String server, String id);
}

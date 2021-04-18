package com.example.demo.service;

import com.example.demo.model.MatchDetailBean;
import com.example.demo.model.MatchesBean;
import com.example.demo.model.UserBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j
public class RiotService implements IRiotService {
    @Value("${API_KEY}")
    private String key;

    @Autowired
    private RestTemplate restTemplate;

    static int counter = 0;

    private final long MILISECONDS_IN_WEEK = 604800000;
    private final long MILISECONDS_IN_THREE_MINUTES = 180000;

    public String getAcccountId(String server, String username) {

        URI uriAccount = UriComponentsBuilder.fromUriString("https://" + server + ".api.riotgames.com/lol/summoner/v4/summoners/by-name/" + username)
            .queryParam("api_key", key)
            .build().toUri();
        log.info("Getting account id for username '" + username + "' on server " + server);
        log.info("" + ++counter);
        UserBean result = restTemplate.getForObject(uriAccount, UserBean.class);
        return result.getAccountId();
    }

    public MatchesBean getMatchHistory(String server, String id) {
        return getMatchHistory(server, id, 20, -1);
    }

    public MatchesBean getMatchHistory(String server, String id, int matches) {
        return getMatchHistory(server, id, matches, -1);
    }

    public MatchesBean getMatchHistory(String server, String id, int matches, long timestamp) {
        int beginIndex = 0, endIndex = matches, queue = 420;
        long beginTime = timestamp - MILISECONDS_IN_WEEK;
        timestamp -= MILISECONDS_IN_THREE_MINUTES;
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("https://" + server + ".api.riotgames.com/lol/match/v4/matchlists/by-account/" + id)
            .queryParam("endIndex", endIndex)
            .queryParam("beginIndex", beginIndex)
            .queryParam("queue", queue)
            .queryParam("api_key", key);
        if (beginTime >= 0) {
            uriComponentsBuilder
                .queryParam("beginTime", beginTime)
                .queryParam("endTime", timestamp);
        }
        URI uriHistory = uriComponentsBuilder.build().toUri();
        log.info(
            "Getting match history for user with id '" + id + "' on server '" + server + "' for " + matches + " matches"
                + (beginTime >= 0 ? " from week before " + timestamp : "")
        );
        try {
            log.info("" + ++counter);
            return restTemplate.getForObject(uriHistory, MatchesBean.class);
        } catch (HttpClientErrorException ex) {
            log.warn("No matches in week before " + timestamp + ". Returning empty list.");
            return new MatchesBean();
        }
    }

    public MatchDetailBean getMatchDetail(String server, String id) {
        URI uriHistory = UriComponentsBuilder.fromUriString("https://" + server + ".api.riotgames.com/lol/match/v4/matches/" + id)
            .queryParam("api_key", key)
            .build().toUri();

        log.info("Getting match detail for match with id '" + id + "' on server " + server);
        log.info("" + ++counter);
        return restTemplate.getForObject(uriHistory, MatchDetailBean.class);
    }

}

package com.example.demo.lol;

import com.example.demo.riot.MatchBean;
import com.example.demo.riot.MatchDetailBean;
import com.example.demo.riot.MatchesBean;
import com.example.demo.riot.ParticipantBean;
import com.example.demo.riot.MatchStats;
import com.example.demo.riot.StatsBean;
import com.example.demo.riot.UserBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class RiotController {

    @Value("${API_KEY}")
    private String key;

    @GetMapping("/exists")
    public ResponseEntity exists(@RequestParam String server, @RequestParam String username) {
        try {
            getAcccountId(server, username);
            return new ResponseEntity(HttpStatus.OK);
        } catch (HttpClientErrorException ex) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/stats")
    public MatchStats[] getStatsForMatches(@RequestParam String server, @RequestParam String username) {
        String userId = getAcccountId(server, username);
        MatchesBean matches = getMatchHistory(server, userId,1);
        MatchStats[] matchStats = new MatchStats[matches.getMatches().length];
        int i = 0, j = 0;
        for (MatchBean match : matches.getMatches()) {
            MatchDetailBean detail = getMatchDetail(server, match.getGameId());
            StatsBean[] statsBeans = new StatsBean[10];
            int userTeam = 1;
            boolean hasUserWon = false;
            for (ParticipantBean participant : detail.getParticipantIdentities()) {
                if (participant.getPlayer().getAccountId().equals(userId)) {
                    userTeam = participant.getParticipantId() <= 5 ? 1 : 2;
                    if (participant.getParticipantId() <= 5 && detail.getTeams()[0].getWin()
                        || participant.getParticipantId() > 5 && detail.getTeams()[1].getWin()) {
                        hasUserWon = true;
                    }
//                    continue;
                }
                int team = participant.getParticipantId() <= 5 ? 1 : 2;
                statsBeans[j++] = getStatsForPlayer(server, participant.getPlayer().getCurrentAccountId(), match.getTimestamp(), participant.getPlayer().getSummonerName(), team);
            }
            matchStats[i++] = new MatchStats(username, userTeam, hasUserWon, statsBeans);
        }
        return matchStats;
    }

    private String getAcccountId(String server, String username)
    {

        final String uriAccount = "https://" + server + ".api.riotgames.com/lol/summoner/v4/summoners/by-name/" + username + "?api_key=" + key;

        RestTemplate restTemplate = new RestTemplate();
        UserBean result = restTemplate.getForObject(uriAccount, UserBean.class);
//        getMatchHistory(server,result.getAccountId());
        return result.getAccountId();
    }

    private MatchesBean getMatchHistory(String server, String id)
    {
        return getMatchHistory(server, id, 20, -1);
    }

    private MatchesBean getMatchHistory(String server, String id, int matches)
    {
        return getMatchHistory(server, id, matches, -1);
    }

    private MatchesBean getMatchHistory(String server, String id, int matches, long timestamp)
    {
        int beginIndex = 0, endIndex = matches, queue = 420;
        long beginTime = timestamp - 604800000;
        timestamp -= 180000;
        String timeParams = beginTime >= 0 ? "&beginTime=" + beginTime + "&endTime=" + timestamp : "";
        final String uriHistory = "https://" + server + ".api.riotgames.com/lol/match/v4/matchlists/by-account/" + id + "?endIndex=" + endIndex + "&beginIndex=" + beginIndex + timeParams + "&queue=" + queue + "&api_key=" + key;

        RestTemplate restTemplate = new RestTemplate();
        try {
            MatchesBean result = restTemplate.getForObject(uriHistory, MatchesBean.class);
            return result;
        } catch (HttpClientErrorException ex) {
            return new MatchesBean();
        }
    }

    private MatchDetailBean getMatchDetail(String server, String id) {
        final String uriHistory = "https://" + server + ".api.riotgames.com/lol/match/v4/matches/" + id + "?api_key=" + key;

        RestTemplate restTemplate = new RestTemplate();
        MatchDetailBean result = restTemplate.getForObject(uriHistory, MatchDetailBean.class);

        return result;
    }

    private StatsBean getStatsForPlayer(String server, String userId, long timestamp, String summonerName, int team) {
        MatchesBean matches = getMatchHistory(server, userId, 1, timestamp);
        int games = 0, wins = 0, streak = 0;
        if(matches.getMatches() == null) {
            return new StatsBean(summonerName, team, games, streak, false, 0);
        }
        boolean isWinStreak = true;
        boolean isStreak = true;
        for (MatchBean match : matches.getMatches()) {
            MatchDetailBean detail = getMatchDetail(server, match.getGameId());
            for (ParticipantBean participant : detail.getParticipantIdentities()) {
                if (participant.getPlayer().getAccountId().equals(userId)) {
                    if (participant.getParticipantId() <= 5 && detail.getTeams()[0].getWin()
                        || participant.getParticipantId() > 5 && detail.getTeams()[1].getWin()) {
                        wins++;
                        if(isStreak) {
                            if(isWinStreak) {
                                streak++;
                            } else {
                                isStreak = false;
                            }
                        }
                    } else if(isStreak) {
                        if(games == 0) {
                            isWinStreak = false;
                        }
                        if(isWinStreak) {
                            isStreak = false;
                        } else {
                            streak++;
                        }
                    }
                    games++;
                    break;
                }
            }
        }
        return new StatsBean(summonerName, team, games, streak, isWinStreak, wins / (double)games);
    }

}

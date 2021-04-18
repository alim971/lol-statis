package com.example.demo.controller;

import com.example.demo.model.MatchBean;
import com.example.demo.model.MatchDetailBean;
import com.example.demo.model.MatchStats;
import com.example.demo.model.MatchesBean;
import com.example.demo.model.ParticipantBean;
import com.example.demo.model.StatsBean;
import com.example.demo.service.IRiotService;
import com.example.demo.service.IStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@Slf4j
//@RequiredArgsConstructor
public class RiotController {

    @Autowired
    private IRiotService riotService;

    @Autowired
    private IStatsService statsService;

    @GetMapping("/exists")
    public ResponseEntity exists(@RequestParam String server, @RequestParam String username) {
        return !getAccountIdIfExists(server, username).equals("") ?
            new ResponseEntity(HttpStatus.OK)
            :
            new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping("/stats")
    public ResponseEntity<MatchStats[]> getStatsForMatches(@RequestParam String server, @RequestParam String username) {
        log.info("Requesting statistics for account with username '" + username + "' on '" + server + "' server");
        String userId = getAccountIdIfExists(server, username);
        if(userId.equals("")) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }
        MatchesBean matches = riotService.getMatchHistory(server, userId, 3);
        MatchStats[] matchStats = new MatchStats[matches.getMatches().length];
        int i = 0;
        for (MatchBean match : matches.getMatches()) {
            MatchDetailBean detail = riotService.getMatchDetail(server, match.getGameId());
            ParticipantBean[] participants = detail.getParticipantIdentities();
            StatsBean[] statsBeans = new StatsBean[participants.length];
            int userTeam = 1;
            boolean hasUserWon = false;
            int j = 0;
            for (ParticipantBean participant : participants) {
                if (participant.getPlayer().getAccountId().equals(userId)) {
                    userTeam = participant.getParticipantId() <= 5 ? 1 : 2;
                    if (participant.getParticipantId() <= 5 && detail.getTeams()[0].getWin()
                        || participant.getParticipantId() > 5 && detail.getTeams()[1].getWin()) {
                        hasUserWon = true;
                    }
//                    continue;
                }
                int team = participant.getParticipantId() <= 5 ? 1 : 2;
                statsBeans[j++] = statsService.getStatsForPlayer(server, participant.getPlayer().getCurrentAccountId(), match.getTimestamp(), participant.getPlayer().getSummonerName(), team);
            }
            matchStats[i++] = new MatchStats(username, userTeam, hasUserWon, statsBeans);
        }
        return ResponseEntity.ok(matchStats);
    }

    private String getAccountIdIfExists(String server, String username) {
        try {
            log.info("Checking whether account with username '" + username + "' exist on '" + server + "' server");
            String id = riotService.getAcccountId(server, username);
            log.info("Account with username '" + username + "' exists on '" + server + "' server");
            return id;
        } catch (HttpClientErrorException ex) {
            log.warn("Account with username '" + username + "' does not exist on '" + server + "' server");

            return "";
        }
    }
}

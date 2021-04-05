package com.example.demo.service;

import com.example.demo.model.MatchBean;
import com.example.demo.model.MatchDetailBean;
import com.example.demo.model.MatchesBean;
import com.example.demo.model.ParticipantBean;
import com.example.demo.model.StatsBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StatsService implements IStatsService {

    @Autowired
    private IRiotService riotService;

    public StatsBean getStatsForPlayer(String server, String userId, long timestamp, String summonerName, int team) {
        log.info(
            "Getting statistics for player with username '" + summonerName + "' on server '" + server + "' before game that"
                + " was played on time " + timestamp
        );

        MatchesBean matches = riotService.getMatchHistory(server, userId, 1, timestamp);
        int games = 0, wins = 0, streak = 0;
        if (matches.getMatches() == null) {
            log.warn("No matches found, returning empty statistics");
            return new StatsBean(summonerName, team, games, streak, false, 0);
        }
        boolean isWinStreak = true;
        boolean isStreak = true;
        for (MatchBean match : matches.getMatches()) {
            MatchDetailBean detail = riotService.getMatchDetail(server, match.getGameId());
            for (ParticipantBean participant : detail.getParticipantIdentities()) {
                if (participant.getPlayer().getCurrentAccountId().equals(userId)) {
                    if (participant.getParticipantId() <= 5 && detail.getTeams()[0].getWin()
                        || participant.getParticipantId() > 5 && detail.getTeams()[1].getWin()) {
                        wins++;
                        if (isStreak) {
                            if (isWinStreak) {
                                streak++;
                            } else {
                                isStreak = false;
                            }
                        }
                    } else if (isStreak) {
                        if (games == 0) {
                            isWinStreak = false;
                        }
                        if (isWinStreak) {
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
        log.info(
            "From last " + games + " game" + (games != 1 ? "s," : ",") + "player '" + summonerName
            + "' has " + wins + " win" + (wins != 1 ? "s" : "") + "and " + streak
                + (isWinStreak ? " win" : " loose") + " streak"
        );
        return new StatsBean(summonerName, team, games, streak, isWinStreak, games > 0 ? wins / (double) games : 0);
    }
}

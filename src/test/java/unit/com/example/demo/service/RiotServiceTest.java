package unit.com.example.demo.service;

import com.example.demo.model.MatchBean;
import com.example.demo.model.MatchDetailBean;
import com.example.demo.model.MatchesBean;
import com.example.demo.model.ParticipantBean;
import com.example.demo.model.StatsBean;
import com.example.demo.model.TeamBean;
import com.example.demo.model.UserBean;
import com.example.demo.service.IRiotService;
import com.example.demo.service.StatsService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiotServiceTest {

    @Mock
    IRiotService riotService;

    @InjectMocks
    StatsService sut;


    @ParameterizedTest
    @CsvSource({"true,1", "false,1", "true,2", "false,2"})
    void getStatsForPlayer(boolean hasUserWon, int team) {
        MatchesBean matchesBean = mock(MatchesBean.class);
        String idToReturn = "foo";
        String username = "bar";
        String server = "server";

        when(riotService.getMatchHistory(server, idToReturn, 1, 0L)).thenReturn(matchesBean);

        MatchBean[] matches = new MatchBean[1];
        matches[0] = mock(MatchBean.class);

        when(matchesBean.getMatches()).thenReturn(matches);
        when(matches[0].getGameId()).thenReturn(idToReturn);

        MatchDetailBean matchDetail = mock(MatchDetailBean.class);

        when(riotService.getMatchDetail(server, matches[0].getGameId())).thenReturn(matchDetail);
        ParticipantBean[] participants = new ParticipantBean[1];
        participants[0] = mock(ParticipantBean.class);
        when(matchDetail.getParticipantIdentities()).thenReturn(participants);

        UserBean userBean = mock(UserBean.class);
        when(participants[0].getPlayer()).thenReturn(userBean);

        when(userBean.getCurrentAccountId()).thenReturn(idToReturn);
        when(participants[0].getParticipantId()).thenReturn((team == 1) ? 1 : 6);

        TeamBean teamBean1 = mock(TeamBean.class);
        TeamBean teamBean2 = mock(TeamBean.class);
        TeamBean[] teams = new TeamBean[2];
        teams[0] = teamBean1;
        teams[1] = teamBean2;
        when(matchDetail.getTeams()).thenReturn(teams);
        if (team == 1) {
            when(teamBean1.getWin()).thenReturn(hasUserWon);
        } else {
            when(teamBean2.getWin()).thenReturn(hasUserWon);
        }


        StatsBean result = sut.getStatsForPlayer(server, idToReturn, 0L, username, team);
        assertEquals(username, result.getSummonerName());
        assertEquals(team, result.getTeam());
        assertEquals(1, result.getStreak());
        assertEquals(hasUserWon ? 1.0 : 0, result.getWinRate());
        assertEquals(1, result.getGames());
    }
}
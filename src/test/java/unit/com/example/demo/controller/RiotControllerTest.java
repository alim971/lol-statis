package unit.com.example.demo.controller;

import com.example.demo.controller.RiotController;
import com.example.demo.model.MatchBean;
import com.example.demo.model.MatchDetailBean;
import com.example.demo.model.MatchStats;
import com.example.demo.model.MatchesBean;
import com.example.demo.model.ParticipantBean;
import com.example.demo.model.StatsBean;
import com.example.demo.model.TeamBean;
import com.example.demo.model.UserBean;
import com.example.demo.service.IRiotService;
import com.example.demo.service.IStatsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RiotControllerTest {

    @InjectMocks
    RiotController sut;

    @Mock
    IRiotService riotService;
    @Mock
    IStatsService statsService;

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(riotService);
    }

    @Test
    void whenUserExistsReturnsOk() {
        when(riotService.getAcccountId(anyString(), anyString())).thenReturn("ok");
        ResponseEntity response = sut.exists("foo", "bar");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void whenUserDoesNotExistsReturnsBadRequest() {
        when(riotService.getAcccountId(anyString(), anyString())).thenThrow(HttpClientErrorException.class);
        ResponseEntity response = sut.exists("foo", "bar");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @CsvSource({"true,1", "false,1", "true,2", "false,2"})
    void itShouldReturnMatchStats(boolean hasUserWon, int team) {
        MatchesBean matchesBean = mock(MatchesBean.class);
        String idToReturn = "foo";
        String username = "bar";
        String server = "server";

        when(riotService.getAcccountId(server, username)).thenReturn(idToReturn);
        when(riotService.getMatchHistory(server, idToReturn, 5)).thenReturn(matchesBean);

//        MatchBean match = mock(MatchBean.class);
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

        when(userBean.getAccountId()).thenReturn(idToReturn);
        when(userBean.getCurrentAccountId()).thenReturn(idToReturn);
        when(userBean.getSummonerName()).thenReturn(username);
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

        StatsBean statsBean = mock(StatsBean.class);
        StatsBean[] players = new StatsBean[1];
        players[0] = statsBean;
        when(statsService.getStatsForPlayer(server, idToReturn, 0L, username, team)).thenReturn(statsBean);

        MatchStats[] result = sut.getStatsForMatches(server, username);
        assertEquals(1, result.length);
        assertEquals(username, result[0].getUserName());
        assertEquals(hasUserWon, result[0].isHasUserWon());
        assertEquals(team, result[0].getUserTeam());
        assertEquals(1, result[0].getPlayers().length);
        assertEquals(statsBean, result[0].getPlayers()[0]);

    }
}
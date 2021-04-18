package integration.com.example.demo.controller;

import com.example.demo.DemoApplication;
import com.google.common.io.Resources;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@ExtendWith({
    MockitoExtension.class,
    SpringExtension.class,
})
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
class RiotControllerIntegrationTest {

    @Value("${API_KEY}")
    private String key;
    private final String server = "eun1";
    private final String username = "lekycae";

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer riotWiremockServer;

    private String getResourceFileContent(String resourceName) throws IOException {
        URL url = Resources.getResource(resourceName);
        return Resources.toString(url, StandardCharsets.UTF_8);
    }

    @BeforeEach
    public void init() {
        riotWiremockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void whenUserExistsReturnsOk() throws IOException {


        String response = getResourceFileContent("getAccountIdResponse.json");
        riotWiremockServer.expect(
            once(), requestTo("https://" + server + ".api.riotgames.com/lol/summoner/v4/summoners/by-name/" + username + "?api_key=" + key))
            .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
        given()
            .port(port)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/exists?server=" + server + "&username=" + username)
            // verify
            .then()
            .log()
            .all()
            .statusCode(HttpStatus.OK.value());

        riotWiremockServer.verify();

    }

    @Test
    void whenUserDoesNotExistsReturnsUnprocessableEntity() throws IOException {
//        riotWiremockServer = MockRestServiceServer.createServer(restTemplate);


        String response = getResourceFileContent("getAccountIdNotFoundResponse.json");

        riotWiremockServer.expect(ExpectedCount.once(),
            requestTo("https://" + server + ".api.riotgames.com/lol/summoner/v4/summoners/by-name/" + username + "?api_key=" + key))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response)
            );

        given()
            .port(port)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/exists?server=" + server + "&username=" + username)
            // verify
            .then()
            .log()
            .all()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());

        riotWiremockServer.verify();

    }


    @Test
    void itShouldReturnMatchStats() throws IOException, ParseException {
        String responseId = getResourceFileContent("getAccountIdResponse.json");
        String accountId = "4QHfmu6i88dGW4y8e2g6W15OeXjSW4HRlUlzvUcnX_5MlqRuKez-2STE";

        int queue = 420, beginIndex = 0, endIndex = 5;
        long endTime = 1617547368623L;
        long beginTime = endTime - 604800000L;
        endTime -=  180000;

        String gameId = "2791181323";
        String matchHistory = getResourceFileContent("getMatchHistoryResponse.json");
        String gameDetail = getResourceFileContent("getGameDetailResponse.json");

        List<String> participantsIds = List.of(
            "A7mZHh22sAOAvHzRi74iWV-r7Kou9TSX-6r2Ryj03zaS9ZU", "AYAppVilWC2-TtjcE-rQft8pz8TXGpB7yfappFw1-ADVnBg",
            "l51Pg0EyiHhKHMYvaQC8MhlGZgak0Bhg7Xj5QnhlP5pq2edvzhsoL2Bm", "fuhJ5M-2M9LFpcpsA_oSkgwiCFtWOESYhm2szrojOHTJuA",
            "0rYuzrNeP9yuK0l9_3i4prp6e3G-76o0CpwumnptNciUhw", "_s5e6JZEwA3K2GJjbx0q5TjnzO91mIQJovCuX2bmvGHyV8M",
            "Wy0HvbAYEQHnci6V3tGFiOAQyoo5OaK_sXw3CRVWHZlLWlM", "4QHfmu6i88dGW4y8e2g6W15OeXjSW4HRlUlzvUcnX_5MlqRuKez-2STE",
            "h1lDpmPGsMrpJ2JTE2IRnFdJbvFGhzF2-fWnz0nV8lMaar4", "UIwgWrl7zg8fWdiNdDczwPOefBm93Y8AQP5UVfXOokorHiWONCyNGSud"
        );

        List<String> gameIds = List.of(
            "2790080818", "2791081712",
            "2791116989", "2791066078",
            "2791115365", "2791147599",
            "2784924043", "2791115267",
            "2791123178", "2791147599"
        );

        List<String> gameDetails = new ArrayList<>();
        List<String> gameHistories = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            gameDetails.add(getResourceFileContent("getGameDetail" + i + "Response.json"));
            gameHistories.add(getResourceFileContent("getStatsMatchHistory" + i + "Response.json"));
        }

        riotWiremockServer.expect(
            once(), requestTo("https://" + server + ".api.riotgames.com/lol/summoner/v4/summoners/by-name/" + username + "?api_key=" + key))
            .andRespond(withSuccess(responseId, MediaType.APPLICATION_JSON));

        riotWiremockServer.expect(
            once(), requestTo("https://" + server + ".api.riotgames.com/lol/match/v4/matchlists/by-account/" + accountId + "?endIndex=" + endIndex + "&beginIndex=" + beginIndex + "&queue=" + queue + "&api_key=" + key))
            .andRespond(withSuccess(matchHistory, MediaType.APPLICATION_JSON));

        riotWiremockServer.expect(
            once(), requestTo("https://" + server + ".api.riotgames.com/lol/match/v4/matches/" + gameId + "?api_key=" + key))
            .andRespond(withSuccess(gameDetail, MediaType.APPLICATION_JSON));

        int i = 0;
        for (String participantId : participantsIds) {
            riotWiremockServer.expect(
                once(), requestTo("https://" + server + ".api.riotgames.com/lol/match/v4/matchlists/by-account/"
                    + participantId + "?endIndex=" + endIndex + "&beginIndex=" + beginIndex + "&queue=" + queue + "&api_key="
                    + key + "&beginTime=" + beginTime + "&endTime=" + endTime))
                .andRespond(withSuccess(gameHistories.get(i), MediaType.APPLICATION_JSON));
            riotWiremockServer.expect(
                once(), requestTo("https://" + server + ".api.riotgames.com/lol/match/v4/matches/" + gameIds.get(i) + "?api_key=" + key))
                .andRespond(withSuccess(gameDetails.get(i), MediaType.APPLICATION_JSON));
            i++;
        }

        JSONArray response = given()
            .port(port)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/stats?server=" + server + "&username=" + username)
            // verify
            .then()
            .log()
            .all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(JSONArray.class);

        JSONParser parser = new JSONParser();
        JSONArray correctResponse = (JSONArray)parser.parse(getResourceFileContent("getCorrectResponse.json"));

        assertEquals(correctResponse.hashCode(), response.hashCode());

        riotWiremockServer.verify();

    }

    @Test
    void itShouldReturnUnprocessableEntity() throws IOException {
        String responseId = getResourceFileContent("getAccountIdNotFoundResponse.json");

        riotWiremockServer.expect(
            once(), requestTo("https://" + server + ".api.riotgames.com/lol/summoner/v4/summoners/by-name/" + username + "?api_key=" + key))
            .andRespond(withStatus(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseId)
            );

       given()
            .port(port)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/stats?server=" + server + "&username=" + username)
            // verify
            .then()
            .log()
            .all()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());

        riotWiremockServer.verify();

    }
}
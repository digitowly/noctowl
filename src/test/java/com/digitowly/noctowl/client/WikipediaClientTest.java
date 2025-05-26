package com.digitowly.noctowl.client;

import com.digitowly.noctowl.client.dto.WikipediaSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WikipediaClientTest {

    private final String baseUrl = "https://en.wikipedia.org/api/rest_v1";

    private MockRestServiceServer mockServer;
    private WikipediaClient wikipediaClient;

    @BeforeEach
    void setUp() {
        var restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        wikipediaClient = new WikipediaClient(restTemplate);
        ReflectionTestUtils.setField(wikipediaClient, "baseUrl", baseUrl);
    }

    @Test
    void getSummary() {
        String title = "Blue_whale";
        String expectedJson = """
            {
              "title": "Blue whale",
              "wikibase_item": "Q157200"
            }
        """;

        String expectedUrl = baseUrl + "/page/summary/Blue_whale";

        mockServer
                .expect(requestTo(expectedUrl))
                .andRespond(withSuccess(expectedJson, org.springframework.http.MediaType.APPLICATION_JSON));

        var result = wikipediaClient.getSummary(title);
        var expected = new WikipediaSummaryDto("Blue whale", "Q157200");
        assertEquals(expected, result);
    }
}

package com.digitowly.noctowl.client;

import com.digitowly.noctowl.client.dto.WikimediaPageDto;
import com.digitowly.noctowl.client.dto.WikimediaPagesDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WikimediaClientTest {

    private final String baseUrl = "https://api.wikimedia.org/core/v1";
    private final String userAgent = "noctowl";

    private MockRestServiceServer mockServer;
    private WikimediaClient wikimediaClient;

    @BeforeEach
    void setUp() {
        var restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        wikimediaClient = new WikimediaClient(restTemplate);
        ReflectionTestUtils.setField(wikimediaClient, "baseUrl", baseUrl);
        ReflectionTestUtils.setField(wikimediaClient, "userAgent", userAgent);
    }

    @Test
    void getPages() {
        String title = "blue whale";
        String expectedJson = """
            {
              "pages": [
                {
                 "id": 12345,
                 "title": "Blue whale",
                 "key": "Blue_whale"
                },
                {
                 "id": 67890,
                 "title": "Blue whale project",
                 "key": "Blue_whale_project"
                }
              ]
            }
        """;

        String expectedUrl = baseUrl + "/wikipedia/en/search/page?q=blue+whale";

        mockServer
                .expect(requestTo(expectedUrl))
                .andExpect(header("User-Agent", userAgent)) // check User-Agent
                .andRespond(withSuccess(expectedJson, org.springframework.http.MediaType.APPLICATION_JSON));

        var result = wikimediaClient.getPages(title);
        var expected = new WikimediaPagesDto(
                List.of(
                        new WikimediaPageDto(12345, "Blue_whale", "Blue whale"),
                        new WikimediaPageDto(67890, "Blue_whale_project", "Blue whale project")
                )
        );
        assertEquals(result, expected);
    }
}

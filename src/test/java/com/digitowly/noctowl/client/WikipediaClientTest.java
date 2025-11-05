package com.digitowly.noctowl.client;

import com.digitowly.noctowl.model.enums.LanguageType;
import com.digitowly.noctowl.model.wikidata.WikipediaSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WikipediaClientTest {

    private final String baseUrl = "https://%s.wikipedia.org/api/rest_v1";
    private final String userAgent = "noctowl-mock";

    private MockRestServiceServer mockServer;
    private WikipediaClient wikipediaClient;

    @BeforeEach
    void setUp() {
        var restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        wikipediaClient = new WikipediaClient(restTemplate);
        ReflectionTestUtils.setField(wikipediaClient, "baseUrl", baseUrl);
        ReflectionTestUtils.setField(wikipediaClient, "userAgent", userAgent);
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

        String expectedUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/Blue_whale";

        mockServer
                .expect(requestTo(expectedUrl))
                .andExpect(header("User-Agent", userAgent))
                .andRespond(withSuccess(expectedJson, org.springframework.http.MediaType.APPLICATION_JSON));

        var result = wikipediaClient.getSummary(title, LanguageType.EN);
        var expected = new WikipediaSummaryDto("Blue whale", "Q157200");
        assertEquals(expected, result);
    }

    @Test
    void getSummary_Error() {
        String expectedUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/boom";
        mockServer
                .expect(requestTo(expectedUrl))
                .andRespond(withException(new IOException()));
        var result = wikipediaClient.getSummary("boom", LanguageType.EN);
        ;
        assertNull(result);
    }
}

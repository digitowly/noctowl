package com.digitowly.noctowl.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WikidataClientTest {

    private final String baseUrl = "https://www.wikidata.org";

    private MockRestServiceServer mockServer;
    private WikidataClient wikidataClient;

    @BeforeEach
    void setUp() {
        var restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        wikidataClient = new WikidataClient(restTemplate);
        ReflectionTestUtils.setField(wikidataClient, "baseUrl", baseUrl);
    }

    @Test
    void getEntity() throws Exception {
        String entityId = "Q42196";
        Path path = Paths.get("src/test/resources/mock/wikidata/blue_whale/wikidata_entity_Q42196_blue_whale.json");
        String expectedJson = Files.readString(path);

        String expectedUrl = baseUrl + "/wiki/Special:EntityData/Q42196.json";

        mockServer
                .expect(requestTo(expectedUrl))
                .andRespond(withSuccess(expectedJson, org.springframework.http.MediaType.APPLICATION_JSON));

        var result = wikidataClient.getEntity(entityId);

        var objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(result);
        var expectedId = root.at("/entities/Q42196/id").asText();

        assertEquals(entityId, expectedId);
    }

    @Test
    void getClaims() throws Exception {
        String entityId = "Q42196";
        Path path = Paths.get("src/test/resources/mock/wikidata/blue_whale/wikidata_claims_Q42196_blue_whale.json");
        String expectedJson = Files.readString(path);

        String expectedUrl = baseUrl + "/w/api.php?action=wbgetclaims&entity=Q42196&format=json";

        mockServer
                .expect(requestTo(expectedUrl))
                .andRespond(withSuccess(expectedJson, org.springframework.http.MediaType.APPLICATION_JSON));

        var result = wikidataClient.getClaims(entityId);

        var objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(result);
        var expected = root.at("/claims/P171").asText();

        assertNotNull(expected);
    }
}

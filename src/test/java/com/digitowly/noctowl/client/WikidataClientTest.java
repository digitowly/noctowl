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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WikidataClientTest {

    private final String baseUrl = "https://www.wikidata.org/wiki";

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
        String entityId = "Blue_whale";
        Path path = Paths.get("src/test/resources/mock/wikidata/wikidata_entity_Q42196.json");
        String expectedJson = Files.readString(path);

        String expectedUrl = baseUrl + "/page/summary/Blue_whale";

        mockServer
                .expect(requestTo(expectedUrl))
                .andRespond(withSuccess(expectedJson, org.springframework.http.MediaType.APPLICATION_JSON));

        var result = wikidataClient.getEntity(entityId);

        var objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(result);
        var expectedId = root.at("/entities/Q42196/id").asText();

        assertEquals("Q42196", expectedId);
    }
}

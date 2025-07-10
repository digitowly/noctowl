package com.digitowly.noctowl.client;

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

class GbifClientTest {

    private final String baseUrl = "https://api.gbif.org/v2";

    private MockRestServiceServer mockServer;
    private GbifClient gbifClient;

    @BeforeEach
    void setUp() {
        var restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        gbifClient = new GbifClient(restTemplate);
        ReflectionTestUtils.setField(gbifClient, "baseUrl", baseUrl);
    }

    @Test
    void getSpecies() throws Exception {
        Path path = Paths.get("src/test/resources/mock/gbif/species/gbif_species_strix_aluco.json");
        String expectedJson = Files.readString(path);

        String expectedUrl = baseUrl + "/species/match?genericName=Strix%20aluco";

        mockServer
                .expect(requestTo(expectedUrl))
                .andRespond(withSuccess(expectedJson, org.springframework.http.MediaType.APPLICATION_JSON));

        var result = gbifClient.getSpecies("Strix aluco");

        // usage
        assertEquals("9282206", result.usage().key());
        assertEquals("Strix aluco Linnaeus, 1758", result.usage().name());
        assertEquals("Strix aluco", result.usage().canonicalName());

        // diagnostics
        assertEquals("EXACT", result.diagnostics().matchType());
        assertEquals(99, result.diagnostics().confidence());
    }
}
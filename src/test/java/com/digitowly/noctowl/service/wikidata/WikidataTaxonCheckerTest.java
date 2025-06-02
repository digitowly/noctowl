package com.digitowly.noctowl.service.wikidata;

import com.digitowly.noctowl.client.WikidataClient;
import com.digitowly.noctowl.model.enums.TaxonType;
import com.digitowly.noctowl.repository.InMemoryTaxonomyTreeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


class WikidataTaxonCheckerTest {

    private final String baseUrl = "https://www.wikidata.org";

    private MockRestServiceServer mockServer;
    private WikidataTaxonChecker checker;

    @BeforeEach
    void setUp() {
        var restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        var wikidataClient = new WikidataClient(restTemplate);
        ReflectionTestUtils.setField(wikidataClient, "baseUrl", baseUrl);

        this.checker = new WikidataTaxonChecker(
                new ObjectMapper(),
                wikidataClient,
                new InMemoryTaxonomyTreeRepository()
        );
    }

    @Test
    void test_is_animal() throws Exception {
        String wikidataBlueWhaleId = "Q42196";

        // Mock Q42196 - Blue Whale
        Path pathQ42196 = Paths.get("src/test/resources/mock/wikidata/blue_whale/wikidata_claims_Q42196_blue_whale.json");
        String jsonQ42196 = Files.readString(pathQ42196);
        String urlQ42196 = baseUrl + "/w/api.php?action=wbgetclaims&entity=Q42196&format=json";

        // Mock Q133320 - Balaenoptera
        Path pathQ133320 = Paths.get("src/test/resources/mock/wikidata/wikidata_claims_Q133320_balaenoptera.json");
        String jsonQ133320 = Files.readString(pathQ133320);
        String urlQ133320 = baseUrl + "/w/api.php?action=wbgetclaims&entity=Q133320&format=json";

        // Mock Q5174 - Eumetazoa
        Path pathQ5174 = Paths.get("src/test/resources/mock/wikidata/wikidata_claims_Q5174_eumetazoa.json");
        String jsonQ5174 = Files.readString(pathQ5174);
        String urlQ5174 = baseUrl + "/w/api.php?action=wbgetclaims&entity=Q5174&format=json";

        mockServer
                .expect(requestTo(urlQ42196))
                .andRespond(withSuccess(jsonQ42196, org.springframework.http.MediaType.APPLICATION_JSON));
        mockServer
                .expect(requestTo(urlQ133320))
                .andRespond(withSuccess(jsonQ133320, org.springframework.http.MediaType.APPLICATION_JSON));
        mockServer
                .expect(requestTo(urlQ5174))
                .andRespond(withSuccess(jsonQ5174, org.springframework.http.MediaType.APPLICATION_JSON));

        var isAnimal = checker.isTaxon(TaxonType.ANIMAL, wikidataBlueWhaleId);
        assertTrue(isAnimal);

        // Test cache hit
        var isAnimalFromMemory = checker.isTaxon(TaxonType.ANIMAL, wikidataBlueWhaleId);
        assertTrue(isAnimalFromMemory);

    }

    @Test
    void test_is_animal_from_subclass() throws Exception {
        String wikidataRatId = "Q26018";

        // Mock Q26018 - Rat
        Path pathQ26018 = Paths.get("src/test/resources/mock/wikidata/wikidata_claims_Q26018_rat.json");
        String jsonQ26018 = Files.readString(pathQ26018);
        String urlQ26018 = baseUrl + "/w/api.php?action=wbgetclaims&entity=Q26018&format=json";

        // Mock Q10850 - Rodentia
        Path pathQ10850 = Paths.get("src/test/resources/mock/wikidata/wikidata_claims_Q10850_rodentia.json");
        String jsonQ10850 = Files.readString(pathQ10850);
        String urlQ10850 = baseUrl + "/w/api.php?action=wbgetclaims&entity=Q10850&format=json";

        mockServer
                .expect(requestTo(urlQ26018))
                .andRespond(withSuccess(jsonQ26018, org.springframework.http.MediaType.APPLICATION_JSON));
        mockServer
                .expect(requestTo(urlQ10850))
                .andRespond(withSuccess(jsonQ10850, org.springframework.http.MediaType.APPLICATION_JSON));

        var isAnimal = checker.isTaxon(TaxonType.ANIMAL, wikidataRatId);
        assertTrue(isAnimal);
    }

    @Test
    void test_is_animal_not_animal() throws Exception {
        String wikidataId = "Q117219396";

        // Mock Q117219396 - Blue Whale Skeleton
        Path pathQ117219396 = Paths.get("src/test/resources/mock/wikidata/wikidata_claims_Q117219396_blue_whale_skeleton.json");
        String jsonQ117219396 = Files.readString(pathQ117219396);
        String urlQ117219396 = baseUrl + "/w/api.php?action=wbgetclaims&entity=Q117219396&format=json";

        mockServer
                .expect(requestTo(urlQ117219396))
                .andRespond(withSuccess(jsonQ117219396, org.springframework.http.MediaType.APPLICATION_JSON));

        var isAnimal = checker.isTaxon(TaxonType.ANIMAL, wikidataId);
        assertFalse(isAnimal);
    }
}
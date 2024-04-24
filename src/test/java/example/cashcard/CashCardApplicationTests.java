package example.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    CashCardRepository cashCardRepository;

    @BeforeEach
    public void setUpTestData() {
        CashCard cashCard1 = new CashCard(100.0, "test1");

        cashCardRepository.save(cashCard1);
    }

    @Test
    void shouldReturnCashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards/1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        Number id = documentContext.read("$.id");
        Number amount = documentContext.read("$.amount");

        assertThat(id).isEqualTo(1);
        assertThat(amount).isEqualTo(100.0);
    }

    @Test
    void shouldReturnNotFoundWithUnknownId() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards/98", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    @DirtiesContext
    void shouldCreateANewCashCard() {
        CashCard newCashCard = new CashCard(250.0, "test");

        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .postForEntity("/cashcards", newCashCard, Void.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity(locationOfNewCashCard, String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnAllCashCardsWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards", String.class);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int cashCardCount = documentContext.read("$.length()");
        JSONArray ids = documentContext.read("$..id");
        JSONArray amounts = documentContext.read("$..amount");



        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cashCardCount).isEqualTo(1);
//        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);
//        assertThat(amounts).containsExactlyInAnyOrder(1.0, 150.0, 123.45);
    }

    @Test
    void shouldReturnAPageOfCashCards() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("sarah1", "abc123")
                .getForEntity("/cashcards?page=0&size=1", String.class);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page.size()).isEqualTo(1);
    }

//    @Test
//    void shouldReturnASortedPageOfCashCards() {
//        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);
//
//        DocumentContext documentContext = JsonPath.parse(response.getBody());
//        JSONArray page = documentContext.read("$[*]");
//        double amount = documentContext.read("$[0].amount");
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(page.size()).isEqualTo(1);
//        assertThat(amount).isEqualTo(150);
//    }
//
//    @Test
//    void shouldReturnASortedListOfCashCardsWithDefaultParameters() {
//        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards", String.class);
//
//        DocumentContext documentContext = JsonPath.parse(response.getBody());
//        JSONArray page = documentContext.read("$[*]");
//        JSONArray amounts = documentContext.read("$..amount");
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(page.size()).isEqualTo(3);
//        assertThat(amounts).containsExactly(1.0, 123.45, 150.0);
//    }

}

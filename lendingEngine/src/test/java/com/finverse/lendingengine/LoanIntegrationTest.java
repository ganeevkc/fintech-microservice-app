package com.finverse.lendingengine;

import com.finverse.lendingengine.dto.LoanApplicationDTO;
import com.finverse.lendingengine.model.Currency;
import com.finverse.lendingengine.model.LoanRequest;
import com.finverse.lendingengine.model.Money;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles(profiles = "test")
public class LoanIntegrationTest {

    private static final String JOHN_UUID = "550e8400-e29b-41d4-a716-446655440000";
    private static final Gson GSON = new Gson();

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int serverPort;

    @Test
    public void givenLoanRequestIsMadeLoanApplicationGetsCreated() throws Exception{
        final String baseURL = "http://localhost:"+serverPort+"/loan";
        HttpHeaders httpHeaders = getHttpHeaders();

        // Fix the LoanRequest constructor call
        Money money = new Money(Currency.USD, 500.0);
        LoanRequest loanRequest = new LoanRequest(money, "Test loan purpose for integration test", 365, 5.2);

        HttpEntity<LoanRequest> request = new HttpEntity<>(loanRequest, httpHeaders);

        testRestTemplate.postForEntity(baseURL+"/request", request, String.class);

        ResponseEntity<String> response = testRestTemplate.exchange(baseURL+"/requests", HttpMethod.GET,
                new HttpEntity(null, getHttpHeaders()), String.class);

        List<LoanApplicationDTO> loanApplicationDTOS = GSON.fromJson(response.getBody(),
                new TypeToken<List<LoanApplicationDTO>>(){}.getType());
        assertEquals(1, loanApplicationDTOS.size());
        // Fix the assertion to use UUID instead of string
        assertEquals(UUID.fromString(JOHN_UUID), loanApplicationDTOS.get(0).getBorrowerId());
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-User-ID", JOHN_UUID); // Use proper UUID format
        httpHeaders.add("X-USER-ROLE", "borrower"); // Add role header
        return httpHeaders;
    }
}
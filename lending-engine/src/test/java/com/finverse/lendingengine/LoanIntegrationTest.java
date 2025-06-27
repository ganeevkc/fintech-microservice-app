//package com.finverse.lendingengine;
//
//import com.finverse.lendingengine.dto.LoanApplicationDTO;
//import com.finverse.lendingengine.model.*;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.http.*;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(
//        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//        properties = {
//                "eureka.client.enabled=false",
//                "spring.cloud.discovery.enabled=false",
//                "spring.cloud.config.enabled=false"
//        }
//)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
//@ActiveProfiles("test")
//@Transactional
//public class LoanIntegrationTest {
//
//    private static final String JOHN_UUID = "550e8400-e29b-41d4-a716-446655440000";
//    private static final Gson GSON = new Gson();
//
//    @Autowired
//    private TestRestTemplate testRestTemplate;
//
//    @LocalServerPort
//    private int serverPort;
//
//    @Test
//    public void givenLoanRequestIsMadeLoanApplicationGetsCreated() throws Exception {
//        final String baseURL = "http://localhost:" + serverPort + "/loan";
//        HttpHeaders httpHeaders = getHttpHeaders();
//
//        // Create loan request
//        Money money = new Money(Currency.USD, 500.0);
//        LoanRequest loanRequest = new LoanRequest(money, "Test loan purpose for integration test", 365, 5.2);
//
//        HttpEntity<LoanRequest> request = new HttpEntity<>(loanRequest, httpHeaders);
//
//        // Make the loan request
//        ResponseEntity<String> postResponse = testRestTemplate.postForEntity(
//                baseURL + "/request", request, String.class);
//
//        // Verify the POST was successful (might be 201 CREATED instead of 200 OK)
//        assertTrue(postResponse.getStatusCode().is2xxSuccessful(),
//                "Expected successful response but got: " + postResponse.getStatusCode());
//
//        // Get all loan applications
//        ResponseEntity<String> getResponse = testRestTemplate.exchange(
//                baseURL + "/requests",
//                HttpMethod.GET,
//                new HttpEntity<>(null, getHttpHeaders()),
//                String.class);
//
//        // Verify the response
//        assertTrue(getResponse.getStatusCode().is2xxSuccessful(),
//                "Expected successful response but got: " + getResponse.getStatusCode());
//        assertNotNull(getResponse.getBody(), "Response body should not be null");
//
//        List<LoanApplicationDTO> loanApplicationDTOS = GSON.fromJson(
//                getResponse.getBody(),
//                new TypeToken<List<LoanApplicationDTO>>(){}.getType());
//
//        assertEquals(1, loanApplicationDTOS.size(), "Should have exactly one loan application");
//        assertEquals(UUID.fromString(JOHN_UUID), loanApplicationDTOS.get(0).getBorrowerId(),
//                "Borrower ID should match the request header");
//    }
//
//    @Test
//    public void givenInvalidLoanRequestShouldReturnBadRequest() throws Exception {
//        final String baseURL = "http://localhost:" + serverPort + "/loan";
//        HttpHeaders httpHeaders = getHttpHeaders();
//
//        // Create invalid loan request (negative amount)
//        Money money = new Money(Currency.USD, -100.0);
//        LoanRequest loanRequest = new LoanRequest(money, "Invalid loan", 365, 5.2);
//
//        HttpEntity<LoanRequest> request = new HttpEntity<>(loanRequest, httpHeaders);
//
//        // Make the loan request - should fail
//        ResponseEntity<String> response = testRestTemplate.postForEntity(
//                baseURL + "/request", request, String.class);
//
//        // Should return 4xx error for invalid request
//        assertTrue(response.getStatusCode().is4xxClientError(),
//                "Expected client error for invalid loan amount");
//    }
//
//    private HttpHeaders getHttpHeaders() {
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//        httpHeaders.add("X-User-ID", JOHN_UUID);
//        httpHeaders.add("X-USER-ROLE", "borrower");
//        return httpHeaders;
//    }
//}
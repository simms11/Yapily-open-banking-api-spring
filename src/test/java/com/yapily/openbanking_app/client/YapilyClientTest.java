package com.yapily.openbanking_app.client;

import com.yapily.openbanking_app.config.YapilyConfig;
import com.yapily.openbanking_app.dto.ConsentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.when;

public class YapilyClientTest {

    @Mock
    private YapilyConfig mockConfig;

    @Mock
    private RestTemplate mockRestTemplate;

    @InjectMocks
    private YapilyClient yapilyClient;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        when(mockConfig.getBaseUrl()).thenReturn("https://api.yapily.com");
        when(mockConfig.getAppId()).thenReturn("test-app-id");
        when(mockConfig.getSecret()).thenReturn("test-secret");

        yapilyClient = new YapilyClient(mockConfig, mockRestTemplate);
    }

    @Test
    void testGetInstitutions_ReturnsExpectedResponse() {
        //Arrange

        String expectedJson = "{\"data\":[]}";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(expectedJson, HttpStatus.OK);

        when(mockRestTemplate.exchange(
                eq("https://api.yapily.com/institutions"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(mockResponse);

        //Act
        ResponseEntity<String> result = yapilyClient.getInstitutions();

        //Assert

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedJson, result.getBody());

    }

    @Test
    void shouldInitiateConsentAndReturnAuthorisationUrl() {
        // Arrange
        ConsentRequest request = new ConsentRequest();
        request.setApplicationUserId("test-user");
        request.setInstitutionId("modelo-sandbox");
        request.setCallback("http://localhost:8080/callback");

        String expectedUrl = "https://api.yapily.com/account-auth-requests";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(
                "{\"data\":{\"authorisationUrl\":\"https://auth.yapily.com/...\"}}",
                HttpStatus.CREATED
        );

        when(mockConfig.getBaseUrl()).thenReturn("https://api.yapily.com");
        when(mockConfig.getAppId()).thenReturn("test-app-id");
        when(mockConfig.getSecret()).thenReturn("test-secret");

        when(mockRestTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(mockResponse);

        // Act
        ResponseEntity<String> response = yapilyClient.initiateConsent(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("authorisationUrl"));
    }

}

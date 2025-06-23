package com.yapily.openbanking_app.client;

import com.yapily.openbanking_app.config.YapilyConfig;
import com.yapily.openbanking_app.dto.ConsentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class YapilyClient {


    private final  YapilyConfig config;
    private final RestTemplate restTemplate;

    @Autowired
    public YapilyClient(YapilyConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate =  restTemplate;
    }

    public ResponseEntity<String> getInstitutions() {
        String url = config.getBaseUrl() + "/institutions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(config.getAppId(), config.getSecret());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }


    /**
     * Initiates a user consent request with the Yapily API for a given institution.
     * This generates an authorisation URL that the user must visit to approve access
     * to their financial data via Open Banking.
     */
    public ResponseEntity<String> initiateConsent(ConsentRequest request) {
        String url = config.getBaseUrl() + "/account-auth-requests";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(config.getAppId(), config.getSecret());

        Map<String, Object> payload = new HashMap<>();
        payload.put("applicationUserId", request.getApplicationUserId());
        payload.put("institutionId", request.getInstitutionId());
        payload.put("callback", request.getCallback());

        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(payload, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    /**
     * Fetches all bank accounts using a stored consent token.
     */
    public ResponseEntity<String> getAccounts(String consentToken) {
        String url = config.getBaseUrl() + "/accounts";

        return getStringResponseEntity(consentToken, url);
    }

    private ResponseEntity<String> getStringResponseEntity(String consentToken, String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(config.getAppId(), config.getSecret());
        headers.set("Consent", consentToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    /**
     * Fetches transactions for a specific account using the consent token.
     */
    public ResponseEntity<String> getTransactions(String consentToken, String accountId) {
        String url = String.format("%s/accounts/%s/transactions", config.getBaseUrl(), accountId);

        return getStringResponseEntity(consentToken, url);
    }

}

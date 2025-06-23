package com.yapily.openbanking_app.controller;

import com.yapily.openbanking_app.client.YapilyClient;
import com.yapily.openbanking_app.dto.ConsentRequest;
import com.yapily.openbanking_app.store.ConsentTokenStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/yapily")
public class YapilyController {

    private final YapilyClient yapilyClient;
    private final ConsentTokenStore tokenStore;

    public YapilyController(
            YapilyClient yapilyClient,
            ConsentTokenStore tokenStore) {
        this.yapilyClient = yapilyClient;
        this.tokenStore   = tokenStore;
    }

    @GetMapping("/institutions")
    public ResponseEntity<String> getInstitutions() {
        return yapilyClient.getInstitutions();
    }

    @PostMapping("/consent")
    public ResponseEntity<String> initiateConsent(
            @RequestBody ConsentRequest request) {
        return yapilyClient.initiateConsent(request);
    }

    @GetMapping("/accounts")
    public ResponseEntity<String> getAccounts(
            @RequestParam String userId) {
        String token = tokenStore.get(userId);
        if (token == null) {
            return ResponseEntity
                    .badRequest()
                    .body("No consent token found for userId=" + userId);
        }
        return yapilyClient.getAccounts(token);
    }

    @GetMapping("/transactions")
    public ResponseEntity<String> getTransactions(
            @RequestParam String userId,
            @RequestParam String accountId) {

        String token = tokenStore.get(userId);
        if (token == null) {
            return ResponseEntity
                    .badRequest()
                    .body("No consent token found for userId=" + userId);
        }
        return yapilyClient.getTransactions(token, accountId);
    }
}

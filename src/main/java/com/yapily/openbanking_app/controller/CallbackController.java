package com.yapily.openbanking_app.controller;

import com.yapily.openbanking_app.store.ConsentTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CallbackController {

    @Autowired
    private ConsentTokenStore tokenStore;

    /**
     * Handles the sandbox redirect from Yapily.
     * Captures the consent token and stores it by userId.
     *
     * Example redirect URL:
     *  /callback?consent=...&application-user-id=alsahid-user-001&user-uuid=...&institution=modelo-sandbox
     *
     * @param consent the actual consent token to use for API calls
     */
    @GetMapping("/callback")
    public ResponseEntity<String> handleCallback(
            @RequestParam("consent") String consent,
            @RequestParam("application-user-id") String applicationUserId) {

        if (consent == null || applicationUserId == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Missing required parameters: consent and application-user-id");
        }

        // Store it in-memory
        tokenStore.save(applicationUserId, consent);

        String msg = String.format(
                "Consent token stored successfully for user '%s'.\n\nToken: %s",
                applicationUserId, consent
        );
        System.out.println(msg);
        return ResponseEntity.ok(msg);
    }
}

package com.yapily.openbanking_app.store;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory store for mapping applicationUserId to consentToken.
 * In a real application this would be replaced by secure DB or Vault storage.
 */
@Component
public class ConsentTokenStore {

    // Thread-safe in-memory store.
    // Using ConcurrentHashMap to support concurrent read/write operations safely
    // across multiple threads in a web application.
    private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

    /**
     * Save the consent token associated with a given application user ID.
     * This token is received after successful user authorisation and will be
     * used for making subsequent API calls on behalf of the user.
     */
    public void save(String userId, String consentToken) {
        store.put(userId, consentToken);
    }

    /**
     * Retrieve the consent token for a specific user.
     * If not found, this may indicate that the user has not completed consent,
     * or that the token was cleared/expired.
     */
    public String get(String userId) {
        return store.get(userId);
    }

    /**
     * Remove the stored consent token for a user.
     * Useful if you're implementing logout, expiry, or revocation flows.
     */
    public void remove(String userId) {
        store.remove(userId);
    }
}

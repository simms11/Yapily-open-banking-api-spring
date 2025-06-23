package com.yapily.openbanking_app.controller;

import com.yapily.openbanking_app.client.YapilyClient;
import com.yapily.openbanking_app.store.ConsentTokenStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class YapilyControllerUnitTest {

    private YapilyClient mockClient;
    private ConsentTokenStore mockStore;
    private YapilyController controller;

    @BeforeEach
    void setup() {
        mockClient = mock(YapilyClient.class);
        mockStore  = mock(ConsentTokenStore.class);
        controller = new YapilyController(mockClient, mockStore);
    }


    @Test
    @DisplayName("getInstitutions() returns 200 and body on success")
    void getInstitutionsSuccess() {
        when(mockClient.getInstitutions())
                .thenReturn(ResponseEntity.ok("[{\"id\":\"modelo-sandbox\"}]"));

        ResponseEntity<String> resp = controller.getInstitutions();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("[{\"id\":\"modelo-sandbox\"}]", resp.getBody());
    }

    @Test
    @DisplayName("getInstitutions() returns error status")
    void getInstitutionsError() {
        when(mockClient.getInstitutions())
                .thenReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("unavail"));

        ResponseEntity<String> resp = controller.getInstitutions();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, resp.getStatusCode());
        assertEquals("unavail", resp.getBody());
    }


    @Test
    @DisplayName("getAccounts() returns 200 when token present")
    void getAccountsSuccess() {
        when(mockStore.get("user1")).thenReturn("tok");
        when(mockClient.getAccounts("tok"))
                .thenReturn(ResponseEntity.ok("{\"data\":[]}"));

        ResponseEntity<String> resp = controller.getAccounts("user1");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("{\"data\":[]}", resp.getBody());
    }

    @Test
    @DisplayName("getAccounts() returns 400 when no token")
    void getAccountsNoToken() {
        when(mockStore.get("user1")).thenReturn(null);

        ResponseEntity<String> resp = controller.getAccounts("user1");

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertTrue(resp.getBody().contains("No consent token found for userId=user1"));
    }

    @Test
    @DisplayName("getAccounts() returns error status")
    void getAccountsError() {
        when(mockStore.get("user1")).thenReturn("tok");
        when(mockClient.getAccounts("tok"))
                .thenReturn(ResponseEntity.status(HttpStatus.FORBIDDEN).body("denied"));

        ResponseEntity<String> resp = controller.getAccounts("user1");

        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
        assertEquals("denied", resp.getBody());
    }


    @Test
    @DisplayName("getTransactions() returns 200 on success")
    void getTransactionsSuccess() {
        when(mockStore.get("user1")).thenReturn("tok");
        when(mockClient.getTransactions("tok", "acc1"))
                .thenReturn(ResponseEntity.ok("{\"data\":[]}"));

        ResponseEntity<String> resp = controller.getTransactions("user1", "acc1");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("{\"data\":[]}", resp.getBody());
    }

    @Test
    @DisplayName("getTransactions() returns 400 when no token")
    void getTransactionsNoToken() {
        when(mockStore.get("user1")).thenReturn(null);

        ResponseEntity<String> resp = controller.getTransactions("user1", "acc1");

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertTrue(resp.getBody().contains("No consent token found for userId=user1"));
    }

    @Test
    @DisplayName("getTransactions() returns error status")
    void getTransactionsError() {
        when(mockStore.get("user1")).thenReturn("tok");
        when(mockClient.getTransactions("tok", "acc1"))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail"));

        ResponseEntity<String> resp = controller.getTransactions("user1", "acc1");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals("fail", resp.getBody());
    }
}

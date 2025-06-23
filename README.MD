
# Yapily Open Banking Demo

This is a Spring Boot application to demonstrate an end-to-end Open Banking integration using Yapily’s sandbox APIs. It covers the complete consent flow, account retrieval, and transaction listing, all with built-in error handling and health checks.

---

## Capabilities

* **Consent Flow**: Users can initiate a consent request (`POST /api/yapily/consent`), capture the consent token via `/callback`, and store it in memory.  
* **Account & Transaction Data**: Users can fetch institutions, user accounts, and transactions via REST endpoints.

---

## Configuration

Update `src/main/resources/application.yml` with the Yapily sandbox credentials and Actuator settings:

```yaml
server:
  port: 8080

yapily:
  base-url: https://api.yapily.com
  app-id: ${YAPILY_APP_ID}
  secret: ${YAPILY_SECRET}

management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
````

> **Note:** The `ApiExceptionHandler` in `com.yapily.openbanking_app.exception` catches and neatly formats all errors thrown by the controllers.

---

## Error Handling

A global `ApiExceptionHandler` (`@ControllerAdvice`) ensures that:

* **Downstream errors (4xx/5xx)** from `YapilyClient` are returned as JSON with `status`, `error`, and `message`.
* **Missing or invalid parameters** (`MissingServletRequestParameterException`, `MethodArgumentTypeMismatchException`) result in a 400 response with a clear message.
* **Unhandled exceptions** produce a 500 response labelled “Internal error”.

---

## Health Checks & Metrics

Spring Boot Actuator is enabled under `/actuator` for observability:

* `GET /actuator/health` → `{ "status": "UP" }`
* `GET /actuator/info` → application metadata
* `GET /actuator/metrics/http.server.requests` → HTTP request metrics

---

## Testing & Quality Gates

* **Unit Tests**: Controller and client logic are verified against mocked dependencies.
* Run all tests with:

  ```bash
  mvn test
  ```

---

## Endpoint Reference

### 1. List Institutions

```
GET /api/yapily/institutions
```

```json
{
  "data": [
    { "id": "modelo-sandbox", "name": "Modelo Sandbox", "environmentType": "SANDBOX" }
  ]
}
```

### 2. Initiate Consent

```
POST /api/yapily/consent
Content-Type: application/json
```

```json
{
  "applicationUserId": "alsahid-user-001",
  "institutionId":   "modelo-sandbox",
  "callback":        "http://localhost:8080/callback"
}
```

### 3. Callback Endpoint

```
GET /callback?consent=CONSENT_TOKEN&application-user-id=alsahid-user-001
```

```
Received callback!

Authorization Code: CONSENT_TOKEN
State: …
```

### 4. List Accounts

```
GET /api/yapily/accounts?userId=alsahid-user-001
```

```json
{
  "data": [
    {
      "id": "100004000000000000000002",
      "type": "Business - Credit_card",
      "balance": 3035.01,
      "currency": "GBP"
    }
  ]
}
```

---

## Running the Application

```bash
mvn clean package
mvn spring-boot:run
```

Verify health:

```bash
curl http://localhost:8080/actuator/health
```

---

## Further Documentation

For more detailed information—including request/response models, authentication flows, and additional endpoints—see the official **[Yapily API documentation](https://docs.yapily.com/)**.

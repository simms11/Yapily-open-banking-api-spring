package com.yapily.openbanking_app.dto;

public class ConsentRequest {

    private String applicationUserId;
    private String institutionId;
    private String callback;

    public String getApplicationUserId() {
        return applicationUserId;
    }

    public void setApplicationUserId(String applicationUserId) {
        this.applicationUserId = applicationUserId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }
}

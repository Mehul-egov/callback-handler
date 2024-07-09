package org.qwikpe.callback.handler.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface ConsentCallbacksService {
    void consentRequestOnFetchModes(JsonNode payload);

    void consentRequestOnAuthInit(JsonNode payload);

    void consentRequestOnConfirm(JsonNode payload);

    void generateToken(JsonNode payload);
}

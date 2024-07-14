package org.qwikpe.callback.handler.service.hip;

import com.fasterxml.jackson.databind.JsonNode;

public interface ConsentCallbacksService {
    void consentRequestOnFetchModes(JsonNode payload);

    void consentRequestOnConfirm(JsonNode payload);

    void onGenerateToken(JsonNode payload, String xHipId);

    void consentRequestInitAuthMode(JsonNode payload);

}

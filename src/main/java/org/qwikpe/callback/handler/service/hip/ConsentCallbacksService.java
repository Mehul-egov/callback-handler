package org.qwikpe.callback.handler.service.hip;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.dto.ConsentRequestHipNotifyDTO;
import org.qwikpe.callback.handler.dto.HipRequestDTO;

import java.io.IOException;
import java.util.Map;

public interface ConsentCallbacksService {
    void consentRequestOnFetchModes(JsonNode payload);

    void consentRequestOnConfirm(JsonNode payload);

    void onGenerateToken(String xHipId, Map<String, Object> requestBody, String abdmRequestId);

    void consentRequestInitAuthMode(JsonNode payload);

    void processConsentRequestHipNotify(ConsentRequestHipNotifyDTO consentRequestHipNotifyDTO, String requestId, String xHipId) throws IOException;

    void hipHealthInformationRequest(HipRequestDTO hipRequestDTO, String xHipId, String requestId) throws IOException;
}

package org.qwikpe.callback.handler.service.hip.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.service.hip.ConsentCallbacksService;
import org.qwikpe.callback.handler.util.Constants;
import org.qwikpe.callback.handler.util.URIMapping;
import org.qwikpe.callback.handler.util.WebClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Service
public class ConsentCallbacksServiceImpl implements ConsentCallbacksService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConsentCallbacksServiceImpl.class);

    @Autowired
    private WebClientUtil webClientUtil;

    @Override
    public void consentRequestOnFetchModes(JsonNode payload) {
        LOGGER.info("consentRequestOnFetchModes :: response: {}", payload);
    }

    @Override
    public void consentRequestInitAuthMode(JsonNode payload) {
        LOGGER.info("consentRequestInitAuthMode :: response: {}", payload);
    }

    @Override
    public void consentRequestOnConfirm(JsonNode payload) {
        LOGGER.info("consentRequestOnConfirm :: response: {}", payload);

        Map<String, Object> registerPatientPayload = new HashMap<>();

        String healthId = payload.get("auth").get("patient").get("id").asText();
        String linkToken = payload.get("auth").get("accessToken").asText();
        registerPatientPayload.put("healthId", healthId);
        registerPatientPayload.put("linkToken", linkToken);

        webClientUtil.postMethod(URIMapping.REGISTRATION_APPLICATION_BASE_URL, URIMapping.REGISTER_PATIENT,
                Collections.emptyMap(), registerPatientPayload, JsonNode.class, Constants.MAX_RETRY);

    }

    @Override
    public void onGenerateToken(JsonNode payload, String xHipId) {
        LOGGER.info("onGenerateToken :: response: {}", payload);

        String healthId = payload.get("abhaAddress").asText();
        String linkToken = payload.get("linkToken").asText();

        Map<String, Object> registerPatientPayload =
                Map.of("healthId", healthId, "linkToken", linkToken, "hipId", xHipId);

        webClientUtil.postMethod(URIMapping.REGISTRATION_APPLICATION_BASE_URL, URIMapping.REGISTER_PATIENT,
                Collections.emptyMap(), registerPatientPayload, JsonNode.class, Constants.MAX_RETRY);
    }


}

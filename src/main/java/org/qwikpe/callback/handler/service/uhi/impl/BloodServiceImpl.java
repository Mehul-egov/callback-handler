package org.qwikpe.callback.handler.service.uhi.impl;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import org.qwikpe.callback.handler.service.uhi.BloodService;
import org.qwikpe.callback.handler.util.Constants;
import org.qwikpe.callback.handler.util.WebClientUtil;
import org.qwikpe.callback.handler.util.uhi.UhiApiResponseComponent;
import org.qwikpe.callback.handler.util.uhi.UhiWebClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

@Builder
@Service
public class BloodServiceImpl implements BloodService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BloodServiceImpl.class);

    @Autowired
    private UhiWebClientUtil webClientUtil;

    @Autowired
    private UhiApiResponseComponent uhiApiResponseComponent;

    public ResponseEntity<JsonNode> setAllBloodData(JsonNode bloodData) {

        ResponseEntity<JsonNode> response = null;
        BufferedReader br = null;
        try {
            Map<String, String> headers = new HashMap<>();

            JsonNode onSearchResponse = webClientUtil.postMethod(Constants.MASTER_URL, Constants.SET_BLOOD_DATA, headers, bloodData, JsonNode.class, 1);

            response = ResponseEntity.ok(onSearchResponse);

        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
        }

        return response;
    }
}

package org.qwikpe.callback.handler.service.uhi.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.service.uhi.HSPAAppointmentService;
import org.qwikpe.callback.handler.util.Constants;
import org.qwikpe.callback.handler.util.WebClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HSPAAppointmentServiceImpl implements HSPAAppointmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HSPAAppointmentServiceImpl.class);

    @Autowired
    private WebClientUtil webClientUtil;

    @Override
    public ResponseEntity<JsonNode> searchDoctorAndSlot(JsonNode jsonNode) {
        try{
            Map<String, String> headers = new HashMap<>();
            String uri;
            if(jsonNode.get("context").get("provider_uri") == null)
                uri = Constants.SEARCH_DOCTOR;
            else
                uri = Constants.SEARCH_SLOT;

            JsonNode onSearchResponse = webClientUtil.postMethod(Constants.APPOINTMENT_BASE_URI, uri, headers, jsonNode, JsonNode.class, 1);

            return ResponseEntity.ok(onSearchResponse);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<JsonNode> selectSlot(JsonNode jsonNode){
        try {
            Map<String, String> headers = new HashMap<>();

            JsonNode onSearchResponse = webClientUtil.postMethod(Constants.APPOINTMENT_BASE_URI, Constants.SELECT_SLOT, headers, jsonNode, JsonNode.class, 1);

            return ResponseEntity.ok(onSearchResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<JsonNode> bookedSlot(JsonNode jsonNode) throws JsonProcessingException {
        try {
            Map<String, String> headers = new HashMap<>();

            JsonNode onSearchResponse = webClientUtil.postMethod(Constants.APPOINTMENT_BASE_URI, Constants.BOOK_SLOT, headers, jsonNode, JsonNode.class, 1);

            return ResponseEntity.ok(onSearchResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

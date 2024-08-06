package org.qwikpe.callback.handler.service.uhi.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.service.uhi.HPRAppointmentService;
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
public class HPRAppointmentServiceImpl implements HPRAppointmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HPRAppointmentServiceImpl.class);

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

            JsonNode onInitResponse = webClientUtil.postMethod(Constants.APPOINTMENT_BASE_URI, Constants.SELECT_SLOT, headers, jsonNode, JsonNode.class, 1);

            return ResponseEntity.ok(onInitResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<JsonNode> bookedSlot(JsonNode jsonNode) {
        try {
            Map<String, String> headers = new HashMap<>();

            JsonNode onConfirmResponse = webClientUtil.postMethod(Constants.APPOINTMENT_BASE_URI, Constants.BOOK_SLOT, headers, jsonNode, JsonNode.class, 1);

            return ResponseEntity.ok(onConfirmResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<JsonNode> cancelSlot(JsonNode jsonNode) {
        try {
            Map<String, String> headers = new HashMap<>();

            JsonNode onCancelResponse = webClientUtil.postMethod(Constants.APPOINTMENT_BASE_URI, Constants.CANCEL_SLOT, headers, jsonNode, JsonNode.class, 1);

            return ResponseEntity.ok(onCancelResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<JsonNode> sendMessage(JsonNode jsonNode) {
        try {
            Map<String, String> headers = new HashMap<>();

            JsonNode onSMessageResponse = webClientUtil.postMethod(Constants.APPOINTMENT_BASE_URI, Constants.MESSAGE, headers, jsonNode, JsonNode.class, 1);

            return ResponseEntity.ok(onSMessageResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<JsonNode> getAppointmentStatus(JsonNode jsonNode) {
        try {
            Map<String, String> headers = new HashMap<>();

            JsonNode onStatusResponse = webClientUtil.postMethod(Constants.APPOINTMENT_BASE_URI, Constants.STATUS, headers, jsonNode, JsonNode.class, 1);

            return ResponseEntity.ok(onStatusResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

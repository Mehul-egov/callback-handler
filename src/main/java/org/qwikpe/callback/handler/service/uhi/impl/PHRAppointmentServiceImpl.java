package org.qwikpe.callback.handler.service.uhi.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.service.uhi.PHRAppointmentService;
import org.qwikpe.callback.handler.util.Constants;
import org.qwikpe.callback.handler.util.uhi.UhiWebClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PHRAppointmentServiceImpl implements PHRAppointmentService {

    @Autowired
    private UhiWebClientUtil webClientUtil;

    @Override
    public ResponseEntity<JsonNode> searchDoctorResponse(JsonNode responseNode) {
        try{
            Map<String, String> headers = new HashMap<>();

            JsonNode onSearchResponse = webClientUtil
                    .postMethod(Constants.PHR_APPOINTMENT_BASE_URI, Constants.DOCTOR_SEARCH_RESPONSE, headers, responseNode, JsonNode.class, 1);

            return ResponseEntity.ok(onSearchResponse);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<JsonNode> searchSlotResponse(JsonNode responseJsonNode) {
        try{
            Map<String, String> headers = new HashMap<>();

            JsonNode onSearchResponse = webClientUtil
                    .postMethod(Constants.PHR_APPOINTMENT_BASE_URI, Constants.SLOT_SEARCH_RESPONSE, headers, responseJsonNode, JsonNode.class, 1);

            return ResponseEntity.ok(onSearchResponse);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}

package org.qwikpe.callback.handler.service.uhi;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

public interface PHRAppointmentService {

    ResponseEntity<JsonNode> searchDoctorResponse(JsonNode responseNode);
    ResponseEntity<JsonNode> searchSlotResponse(JsonNode responseJsonNode);
}

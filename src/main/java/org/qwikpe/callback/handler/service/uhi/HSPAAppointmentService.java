package org.qwikpe.callback.handler.service.uhi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface HSPAAppointmentService {
    ResponseEntity<JsonNode> searchDoctorAndSlot(JsonNode jsonNode);
    ResponseEntity<JsonNode> selectSlot(JsonNode jsonNode);
    ResponseEntity<JsonNode> bookedSlot(JsonNode jsonNode);
    ResponseEntity<JsonNode> cancelSlot(JsonNode jsonNode);
    ResponseEntity<JsonNode> sendMessage(JsonNode jsonNode);
    ResponseEntity<JsonNode> getAppointmentStatus(JsonNode jsonNode);
}

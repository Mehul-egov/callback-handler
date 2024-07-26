package org.qwikpe.callback.handler.service.uhi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface HSPAAppointmentService {
    ResponseEntity<JsonNode> searchDoctorAndSlot(JsonNode jsonNode) throws JsonProcessingException;
    ResponseEntity<JsonNode> selectSlot(JsonNode jsonNode) throws JsonProcessingException;
    ResponseEntity<JsonNode> bookedSlot(JsonNode jsonNode) throws JsonProcessingException;
    ResponseEntity<JsonNode> cancelSlot(JsonNode jsonNode) throws JsonProcessingException;
    ResponseEntity<JsonNode> sendMessage(JsonNode jsonNode) throws JsonProcessingException;
    ResponseEntity<JsonNode> getAppointmentStatus(JsonNode jsonNode) throws JsonProcessingException;
}

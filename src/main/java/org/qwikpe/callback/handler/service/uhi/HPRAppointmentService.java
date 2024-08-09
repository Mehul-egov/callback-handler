package org.qwikpe.callback.handler.service.uhi;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface HPRAppointmentService {
    ResponseEntity<JsonNode> callHprTeleconsultationApi(JsonNode jsonNode);
}

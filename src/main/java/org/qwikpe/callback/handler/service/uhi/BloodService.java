package org.qwikpe.callback.handler.service.uhi;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface BloodService {
    ResponseEntity<JsonNode> setAllBloodData(JsonNode bloodData);
}

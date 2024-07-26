package org.qwikpe.callback.handler.service.uhi;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface UHICommonService {

    void searchResponse(String payload);
    ResponseEntity<JsonNode> searchRequest(String payload, HttpServletRequest httpServletRequest);
    ResponseEntity<JsonNode> initRequest(String payload, HttpServletRequest httpServletRequest);
    ResponseEntity<JsonNode> confirmRequest(String payload, HttpServletRequest httpServletRequest);
    ResponseEntity<JsonNode> cancelRequest(String payload, HttpServletRequest httpServletRequest);
    ResponseEntity<JsonNode> messageRequest(String payload, HttpServletRequest httpServletRequest);
    ResponseEntity<JsonNode> statusRequest(String payload, HttpServletRequest httpServletRequest);
}

package org.qwikpe.callback.handler.service.uhi;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface UHICommonService {

    ResponseEntity<JsonNode> phrApiResponse(String payload,HttpServletRequest httpServletRequest);
    ResponseEntity<JsonNode> messageResponse(String payload,HttpServletRequest httpServletRequest);

    ResponseEntity<JsonNode> hprApiRequest(String payload,HttpServletRequest httpServletRequest);
    ResponseEntity<JsonNode> messageRequest(String payload,HttpServletRequest httpServletRequest);
}

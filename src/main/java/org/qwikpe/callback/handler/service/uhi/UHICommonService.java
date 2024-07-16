package org.qwikpe.callback.handler.service.uhi;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface UHICommonService {

    void searchResponse(String payload);
    ResponseEntity<JsonNode> searchRequest(String payload);
    ResponseEntity<JsonNode> initRequest(String payload);
    ResponseEntity<JsonNode> confirmRequest(String payload);

    ResponseEntity<JsonNode> getError();
}

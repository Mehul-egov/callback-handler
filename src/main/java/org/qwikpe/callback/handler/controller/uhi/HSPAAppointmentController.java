package org.qwikpe.callback.handler.controller.uhi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.qwikpe.callback.handler.service.uhi.HSPAAppointmentService;
import org.qwikpe.callback.handler.service.uhi.UHICommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HSPAAppointmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HSPAAppointmentController.class);

    @Autowired
    private UHICommonService uhiCommonService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/search")
    public ResponseEntity<JsonNode> search(@RequestBody String payload) {

        ResponseEntity<JsonNode> response = null;
        try {
            LOGGER.info("Search payload EUA request :: {}", payload);
            response = uhiCommonService.searchRequest(payload);
        }
        catch (Exception e) {
//            LOGGER.info(""+e);
            response = uhiCommonService.getError();
        }

        return response;
    }

    @PostMapping("/init")
    public ResponseEntity<JsonNode> init(@RequestBody String payload) {

        ResponseEntity<JsonNode> response = null;
        try {
            LOGGER.info("Init payload EUA request :: {}", payload);
            response = uhiCommonService.initRequest(payload);
        }
        catch (Exception e) {
//            LOGGER.info(""+e);
            response = uhiCommonService.getError();
        }

        return response;
    }

    @PostMapping("/confirm")
    public ResponseEntity<JsonNode> confirm(@RequestBody String payload) {

        ResponseEntity<JsonNode> response = null;
        try {
            LOGGER.info("Confirm payload EUA request :: {}", payload);
            response = uhiCommonService.confirmRequest(payload);
        }
        catch (Exception e) {
//            LOGGER.info(""+e);
            response = uhiCommonService.getError();
        }

        return response;
    }
}

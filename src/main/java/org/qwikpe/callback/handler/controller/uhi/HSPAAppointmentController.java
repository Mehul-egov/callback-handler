package org.qwikpe.callback.handler.controller.uhi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.qwikpe.callback.handler.service.uhi.HSPAAppointmentService;
import org.qwikpe.callback.handler.service.uhi.UHICommonService;
import org.qwikpe.callback.handler.util.uhi.UhiApiResponseComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.netty.http.server.HttpServerRequest;

@RestController
public class HSPAAppointmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HSPAAppointmentController.class);

    @Autowired
    private UHICommonService uhiCommonService;

    @Autowired
    private UhiApiResponseComponent uhiApiResponseComponent;

    @PostMapping("/search")
    public ResponseEntity<JsonNode> search(@RequestBody String payload, HttpServletRequest httpServletRequest) {

        ResponseEntity<JsonNode> response = null;
        try {
            LOGGER.info("Search payload EUA request :: {}", payload);
            response = uhiCommonService.searchRequest(payload,httpServletRequest);
        }
        catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
        }

        return response;
    }

    @PostMapping("/init")
    public ResponseEntity<JsonNode> init(@RequestBody String payload, HttpServletRequest httpServletRequest) {

        ResponseEntity<JsonNode> response = null;
        try {
            LOGGER.info("Init payload EUA request :: {}", payload);
            response = uhiCommonService.initRequest(payload,httpServletRequest);
        }
        catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
        }

        return response;
    }

    @PostMapping("/confirm")
    public ResponseEntity<JsonNode> confirm(@RequestBody String payload, HttpServletRequest httpServletRequest) {

        ResponseEntity<JsonNode> response = null;
        try {
            LOGGER.info("Confirm payload EUA request :: {}", payload);
            response = uhiCommonService.confirmRequest(payload,httpServletRequest);
        }
        catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
        }

        return response;
    }

    @PostMapping("/cancel")
    public ResponseEntity<JsonNode> cancel(@RequestBody String payload, HttpServletRequest httpServletRequest) {

        ResponseEntity<JsonNode> response = null;
        try {
            LOGGER.info("Cancel payload EUA request :: {}", payload);
            response = uhiCommonService.cancelRequest(payload,httpServletRequest);
        }
        catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
        }

        return response;
    }

    @PostMapping("/status")
    public ResponseEntity<JsonNode> status(@RequestBody String payload, HttpServletRequest httpServletRequest) {

        ResponseEntity<JsonNode> response = null;
        try {
            LOGGER.info("Status payload EUA request :: {}", payload);
            response = uhiCommonService.statusRequest(payload,httpServletRequest);
        }
        catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
        }

        return response;
    }
}

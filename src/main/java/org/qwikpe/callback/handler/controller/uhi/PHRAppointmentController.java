package org.qwikpe.callback.handler.controller.uhi;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
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

@RestController
public class PHRAppointmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PHRAppointmentController.class);

    @Autowired
    private UHICommonService uhiCommonService;

    @Autowired
    private UhiApiResponseComponent uhiApiResponseComponent;

    @PostMapping(value = "/on_search")
    public ResponseEntity<JsonNode> onSearch(@RequestBody String payload, HttpServletRequest httpServletRequest) {

        ResponseEntity<JsonNode> response = null;
        try {
            LOGGER.info("onSearch :: payload: {}", payload);
            response = uhiCommonService.searchResponse(payload,httpServletRequest);
        } catch (Exception e) {
            LOGGER.error("onSearch :: Error", e);
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
        }

        return response;
    }

    @PostMapping(value = "/on_select")
    public void onSelect(@RequestBody String payload) {
        try {
            LOGGER.info("onSelect :: payload: {}", payload);
        } catch (Exception e) {
            LOGGER.error("onSelect :: Error", e);
        }
    }

    @PostMapping(value = "/on_init")
    public void onInit(@RequestBody String payload) {
        try {
            LOGGER.info("onInit :: payload: {}", payload);
        } catch (Exception e) {
            LOGGER.error("onInit :: Error", e);
        }
    }

    @PostMapping(value = "/on_confirm")
    public void onConfirm(@RequestBody String payload) {
        try {
            LOGGER.info("onConfirm :: payload: {}", payload);
        } catch (Exception e) {
            LOGGER.error("onConfirm :: Error", e);
        }
    }

    @PostMapping(value = "/on_status")
    public void onStatus(@RequestBody String payload) {
        try {
            LOGGER.info("onStatus :: payload: {}", payload);
        } catch (Exception e) {
            LOGGER.error("onStatus :: Error", e);
        }
    }

    @PostMapping(value = "/on_update")
    public void onUpdate(@RequestBody String payload) {
        try {
            LOGGER.info("onUpdate :: payload: {}", payload);
        } catch (Exception e) {
            LOGGER.error("onUpdate :: Error", e);
        }
    }

    @PostMapping(value = "/on_message")
    public ResponseEntity<JsonNode> onMessage(@RequestBody String payload, HttpServletRequest httpServletRequest) {

        ResponseEntity<JsonNode> response = null;
        try {
            LOGGER.info("onMessage :: payload: {}", payload);
            response = uhiCommonService.messageRequest(payload,httpServletRequest);
        } catch (Exception e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
        }
        return response;
    }

    @PostMapping(value = "/on_cancel")
    public void onCancel(@RequestBody String payload) {
        try {
            LOGGER.info("onCancel :: payload: {}", payload);
        } catch (Exception e) {
            LOGGER.error("onCancel :: Error", e);
        }
    }
}

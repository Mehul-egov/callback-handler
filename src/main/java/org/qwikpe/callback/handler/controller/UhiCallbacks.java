package org.qwikpe.callback.handler.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.service.uhi.UHICommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UhiCallbacks {
    private static final Logger LOGGER = LoggerFactory.getLogger(UhiCallbacks.class);

    @Autowired
    private UHICommonService uhiCommonService;

    @PostMapping(value = "/on_search")
    public void onSearch(@RequestBody String payload) {
        try {
            LOGGER.info("onSearch :: payload: {}", payload);
            uhiCommonService.searchResponse(payload);
        } catch (Exception e) {
            LOGGER.error("onSearch :: Error", e);
        }
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
    public void onMessage(@RequestBody String payload) {
        try {
            LOGGER.info("onMessage :: payload: {}", payload);
        } catch (Exception e) {
            LOGGER.error("onMessage :: Error", e);
        }
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

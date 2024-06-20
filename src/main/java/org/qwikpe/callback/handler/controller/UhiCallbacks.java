package org.qwikpe.callback.handler.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UhiCallbacks {
    private static final Logger LOGGER = LoggerFactory.getLogger(UhiCallbacks.class);

    @PostMapping(value = "/on_search")
    public void onSearch(@RequestBody JsonNode payload) {
        try {
            LOGGER.info("onSearch :: payload: {}", payload);
        } catch (Exception e) {
            LOGGER.error("onSearch :: Error", e);
        }
    }
}

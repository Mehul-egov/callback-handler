package org.qwikpe.callback.handler.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.qwikpe.callback.handler.service.ConsentCallbacksService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping
public class ConsentCallbacks {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsentCallbacks.class);

    @Autowired
    private ConsentCallbacksService consentCallbacksService;

    @PostMapping(value = "/v0.5/users/auth/on-fetch-modes")
    public void consentRequestOnInit(@RequestBody JsonNode payload) {
        try {
            consentCallbacksService.consentRequestOnInit(payload);
        } catch (Exception e) {
            LOGGER.error("consentRequestOnInit :: Error while processing the callback response, payload: {}", payload, e);
        }
    }
}

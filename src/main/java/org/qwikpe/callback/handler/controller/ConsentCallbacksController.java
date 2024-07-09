package org.qwikpe.callback.handler.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.service.ConsentCallbacksService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping
public class ConsentCallbacksController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsentCallbacksController.class);

    @Autowired
    private ConsentCallbacksService consentCallbacksService;

    @PostMapping(value = "/v0.5/users/auth/on-fetch-modes")
    public void consentRequestFetchAuthMode(@RequestBody JsonNode payload) {
        try {
            consentCallbacksService.consentRequestOnFetchModes(payload);
        } catch (Exception e) {
            LOGGER.error("consentRequestFetchAuthMode :: Error while processing the callback response, payload: {}", payload, e);
        }
    }

    @PostMapping(value = "/v0.5/users/auth/on-auth-init")
    public void consentRequestAuthInit(@RequestBody JsonNode payload) {
        try {
            consentCallbacksService.consentRequestOnAuthInit(payload);
        } catch (Exception e) {
            LOGGER.error("consentRequestAuthInit :: Error while processing the callback response, payload: {}", payload, e);
        }
    }

    @PostMapping(value = "/v0.5/users/auth/on-confirm")
    public void consentRequestOnConfirm(@RequestBody JsonNode payload) {
        try {
            consentCallbacksService.consentRequestOnConfirm(payload);
        } catch (Exception e) {
            LOGGER.error("consentRequestOnConfirm :: Error while processing the callback response, payload: {}", payload, e);
        }
    }

    @PostMapping(value = "/api/v3/hip/token/on-generate-token")
    public void generateToken(@RequestBody JsonNode payload) {
        try {
            consentCallbacksService.generateToken(payload);
        } catch (Exception e) {
            LOGGER.error("generateToken :: Error while processing the callback response, payload: {}", payload, e);
        }
    }

    @PostMapping(value = "/v0.5/subscription-requests/hiu/on-init")
    public void hiuOnInit(@RequestBody JsonNode payload) {
        try {
            LOGGER.info("hiuOnInit :: payload: {}", payload);
        } catch (Exception e) {
            LOGGER.error("generateTokenError :: Error while processing the callback response, payload: {}", payload, e);
        }
    }
}

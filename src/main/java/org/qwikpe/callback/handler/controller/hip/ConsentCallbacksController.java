package org.qwikpe.callback.handler.controller.hip;

import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.service.hip.ConsentCallbacksService;
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

    @PostMapping(value = "/v0.5/users/auth/on-init")
    public void consentRequestInitAuthMode(@RequestBody JsonNode payload) {
        try {
            consentCallbacksService.consentRequestInitAuthMode(payload);
        } catch (Exception e) {
            LOGGER.error("consentRequestInitAuthMode :: Error while processing the callback response, payload: {}", payload, e);
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
    public void onGenerateToken(@RequestBody JsonNode payload,
                                @RequestHeader("X-HIP-ID") String xHipID) {
        try {
            consentCallbacksService.onGenerateToken(payload, xHipID);
        } catch (Exception e) {
            LOGGER.error("onGenerateToken :: Error while processing the callback response of on-generate-token, payload: {}", payload, e);
        }
    }

    //Todo: Need toe relocate in phr
    @PostMapping(value = "/v0.5/subscription-requests/hiu/on-init")
    public void hiuOnInit(@RequestBody JsonNode payload) {
        try {
            LOGGER.info("hiuOnInit :: payload: {}", payload);
        } catch (Exception e) {
            LOGGER.error("generateTokenError :: Error while processing the callback response, payload: {}", payload, e);
        }
    }
}

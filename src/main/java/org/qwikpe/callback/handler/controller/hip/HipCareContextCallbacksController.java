package org.qwikpe.callback.handler.controller.hip;

import org.qwikpe.callback.handler.service.hip.HipCareContextCallbacksService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
public class HipCareContextCallbacksController {

    public static final Logger LOGGER = LoggerFactory.getLogger(HipCareContextCallbacksController.class);

    @Autowired
    private HipCareContextCallbacksService hipCareContextCallbacksService;

    @PostMapping(value = "/api/v3/link/on_carecontext")
    public void onCareContext(@RequestBody Map<String, Object> requestBody) {

        try {
            hipCareContextCallbacksService.onCareContext(requestBody);
        } catch (Exception e) {
            LOGGER.error("onCareContext :: Error while processing the request body of onCareContext: {}", requestBody, e);
        }
    }

    @PostMapping(value = "/api/v3/hip/patient/care-context/discover")
    public void discoverCareContext(@RequestBody Map<String, Object> requestBody, @RequestHeader("X-HIP-ID") String xHipId) {
        try {
            hipCareContextCallbacksService.discoverCareContext(requestBody, xHipId);
        } catch (Exception e) {
            LOGGER.error("discoverCareContext :: Error while processing the request body of discoverCareContext: {}", requestBody, e);
        }
    }

    @PostMapping(value = "/api/v3/hip/link/care-context/init")
    public void initCareContext(@RequestBody Map<String, Object> requestBody) {
        try {
            hipCareContextCallbacksService.initCareContext(requestBody);
        } catch (Exception e) {
            LOGGER.error("initCareContext :: Error while processing the request body of initCareContext: {}", requestBody, e);
        }
    }
}

package org.qwikpe.callback.handler.controller.phr;

import org.qwikpe.callback.handler.service.phr.PhrCareContextCallbacksService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PhrCareContextCallbacksController {

    public static final Logger LOGGER = LoggerFactory.getLogger(PhrCareContextCallbacksController.class);

    @Autowired
    private PhrCareContextCallbacksService phrCareContextCallbacksService;

    @PostMapping(value = "/api/v3/app/patient/care-context/on-discover")
    public void onDiscoverCareContext(@RequestBody Map<String, Object> requestBody) {
        try {
            phrCareContextCallbacksService.onDiscoverCareContext(requestBody);
        } catch (Exception e) {
            LOGGER.error("onDiscoverCareContext :: Error while processing the callback response, payload: {}", requestBody, e);

        }
    }
}

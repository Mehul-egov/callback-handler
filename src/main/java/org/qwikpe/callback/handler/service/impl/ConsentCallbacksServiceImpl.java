package org.qwikpe.callback.handler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.service.ConsentCallbacksService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class ConsentCallbacksServiceImpl implements ConsentCallbacksService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConsentCallbacksServiceImpl.class);

    @Override
    public void consentRequestOnInit(JsonNode payload) {
        LOGGER.info("response: {}", payload);
    }
}

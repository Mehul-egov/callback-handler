package org.qwikpe.callback.handler.service.hip.impl;

import org.qwikpe.callback.handler.service.hip.HipCareContextCallbacksService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HipCareContextCallbacksServiceImpl implements HipCareContextCallbacksService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HipCareContextCallbacksServiceImpl.class);

    @Override
    public void onCareContext(Map<String, Object> requestBody) {

    }

    @Override
    public void discoverCareContext(Map<String, Object> requestBody, String HipId) {
        LOGGER.info("discoverCareContext :: requestBody: {}", requestBody);
    }

    @Override
    public void initCareContext(Map<String, Object> requestBody) {
        LOGGER.info("initCareContext :: requestBody: {}", requestBody);
    }
}

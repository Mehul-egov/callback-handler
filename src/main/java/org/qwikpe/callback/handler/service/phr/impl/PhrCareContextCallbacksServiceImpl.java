package org.qwikpe.callback.handler.service.phr.impl;

import org.qwikpe.callback.handler.service.phr.PhrCareContextCallbacksService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PhrCareContextCallbacksServiceImpl implements PhrCareContextCallbacksService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhrCareContextCallbacksServiceImpl.class);

    @Override
    public void onDiscoverCareContext(Map<String, Object> requestBody) {
        LOGGER.info("onDiscoverCareContext :: requestBody: {}", requestBody);
    }
}

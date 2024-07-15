package org.qwikpe.callback.handler.service.uhi.impl;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import org.qwikpe.callback.handler.service.uhi.BloodService;
import org.qwikpe.callback.handler.util.Constants;
import org.qwikpe.callback.handler.util.WebClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Builder
@Service
public class BloodServiceImpl implements BloodService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BloodServiceImpl.class);

    @Autowired
    private WebClientUtil webClientUtil;

    public void setAllBloodData(JsonNode bloodData) {

        BufferedReader br = null;
        try {
            Map<String, String> headers = new HashMap<>();

            String onSearchResponse = webClientUtil.postMethod(Constants.MASTER_URL, Constants.SET_BLOOD_DATA, headers, bloodData, String.class, 1);

            LOGGER.info("set Blood data response :: {}", onSearchResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

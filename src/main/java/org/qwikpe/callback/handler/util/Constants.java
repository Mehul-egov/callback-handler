package org.qwikpe.callback.handler.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;

@Controller
public class Constants {
    public static final int MAX_RETRY = 2;

    public static final String UHI_BASE_URL = "https://uhigatewaysandbox.abdm.gov.in/api/v1";

    public static final String UHI_ON_SEARCH = "/on_search";

    public static final String MASTER_URL = "http://localhost:9093";

    public static final String SET_BLOOD_DATA = "/blood-data/set";
}


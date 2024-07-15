package org.qwikpe.callback.handler.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class Common {

    public static ObjectMapper JACK_OBJ_MAPPER;

    @PostConstruct
    public void init() {
        JACK_OBJ_MAPPER = new ObjectMapper();
    }

}

package org.qwikpe.callback.handler.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class Common {

    public static ObjectMapper JACK_OBJ_MAPPER;

    @PostConstruct
    public void init() {
        JACK_OBJ_MAPPER = new ObjectMapper();
    }

    public static Map<String, String> headersWithUtcTimestampAndRequestIdAndCmId() {
        OffsetDateTime currentUTCTimeStamp = OffsetDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendInstant(3).toFormatter();
        String isoUtcString = currentUTCTimeStamp.format(formatter);
        return new HashMap<>(){{
            put(Constants.REQUEST_ID, UUID.randomUUID().toString());
            put(Constants.TIMESTAMP, isoUtcString);
            put(Constants.X_CM_ID, "sbx");
        }};
    }


}

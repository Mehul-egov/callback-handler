package org.qwikpe.callback.handler.configration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfigure {

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}

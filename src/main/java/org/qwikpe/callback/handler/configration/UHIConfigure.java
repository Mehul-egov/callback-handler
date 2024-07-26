package org.qwikpe.callback.handler.configration;

import org.qwikpe.callback.handler.util.uhi.HeaderGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UHIConfigure {

    @Bean
    public HeaderGenerator getHeaderGenerator() {
        return new HeaderGenerator();
    }
}

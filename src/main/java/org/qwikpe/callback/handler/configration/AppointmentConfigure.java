package org.qwikpe.callback.handler.configration;

import org.qwikpe.callback.handler.util.HeaderGenerator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppointmentConfigure {

    @Bean
    public HeaderGenerator getHeaderGenerator() {
        return new HeaderGenerator();
    }
}

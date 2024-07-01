package org.qwikpe.callback.handler.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.qwikpe.callback.handler.service.HSPAAppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HSPAAppointmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HSPAAppointmentController.class);

    @Autowired
    private HSPAAppointmentService hspaAppointmentService;

    @PostMapping("/search")
    public void search(@RequestBody String payload) {

        try {
            LOGGER.info("Search payload EUA request :: {}", payload);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(payload);

            if(jsonNode.get("context").get("provider_uri") == null)
                hspaAppointmentService.searchDoctor(jsonNode);
            else
                hspaAppointmentService.secondSearch(jsonNode);
        }
        catch (Exception e) {
            LOGGER.info(""+e);
        }
    }
}

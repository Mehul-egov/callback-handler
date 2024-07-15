package org.qwikpe.callback.handler.service.uhi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface HSPAAppointmentService {
    public void searchDoctor(JsonNode jsonNode) throws JsonProcessingException;
    public void secondSearch(JsonNode jsonNode) throws JsonProcessingException;
}

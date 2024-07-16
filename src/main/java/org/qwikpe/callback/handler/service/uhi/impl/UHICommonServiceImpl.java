package org.qwikpe.callback.handler.service.uhi.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.qwikpe.callback.handler.domain.uhi.CredentialsInfo;
import org.qwikpe.callback.handler.repository.uhi.CredentialsInfoRepository;
import org.qwikpe.callback.handler.service.uhi.BloodService;
import org.qwikpe.callback.handler.service.uhi.HSPAAppointmentService;
import org.qwikpe.callback.handler.service.uhi.UHICommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UHICommonServiceImpl implements UHICommonService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BloodService bloodService;

    @Autowired
    private CredentialsInfoRepository credentialsInfoRepository;

    @Autowired
    private HSPAAppointmentService hspaAppointmentService;

    @Override
    public void searchResponse(String payload) {

        try {
            JsonNode jsonNode = objectMapper.readTree(payload);

            CredentialsInfo credentialsInfo = credentialsInfoRepository.findByDomainAndSubscriberId(
                    jsonNode.get("context").get("domain").asText(), jsonNode.get("context").get("consumer_id").asText()
            );

            switch(credentialsInfo.getDomainName()) {

                case "Blood Banks" :
                    if(jsonNode.get("context").get("provider_id").asText().equals("cdac.hspa")) {
                        bloodService.setAllBloodData(jsonNode);
                    }
                    break;

                case "TeleConsultation" :
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<JsonNode> searchRequest(String payload) {

        ResponseEntity<JsonNode> response = null;
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);

            CredentialsInfo credentialsInfo = credentialsInfoRepository.findByDomainAndSubscriberId(
                    jsonNode.get("context").get("domain").asText(), jsonNode.get("context").get("provider_id").asText()
            );

            if(credentialsInfo == null) {
                response = this.getError();
            } else {
                switch(credentialsInfo.getDomainName()) {

                    case "Blood Banks" :
                        break;

                    case "TeleConsultation" :
                        response = hspaAppointmentService.searchDoctorAndSlot(jsonNode);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseEntity<JsonNode> initRequest(String payload) {

        ResponseEntity<JsonNode> response = null;
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);

            CredentialsInfo credentialsInfo = credentialsInfoRepository.findByDomainAndSubscriberId(
                    jsonNode.get("context").get("domain").asText(), jsonNode.get("context").get("provider_id").asText()
            );

            if(credentialsInfo == null) {
                response = this.getError();
            } else {
                switch(credentialsInfo.getDomainName()) {

                    case "Blood Banks" :
                        break;

                    case "TeleConsultation" :
                        response = hspaAppointmentService.selectSlot(jsonNode);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseEntity<JsonNode> confirmRequest(String payload) {

        ResponseEntity<JsonNode> response = null;
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);

            CredentialsInfo credentialsInfo = credentialsInfoRepository.findByDomainAndSubscriberId(
                    jsonNode.get("context").get("domain").asText(), jsonNode.get("context").get("provider_id").asText()
            );

            if(credentialsInfo == null) {
                response = this.getError();
            } else {
                switch(credentialsInfo.getDomainName()) {

                    case "Blood Banks" :
                        break;

                    case "TeleConsultation" :
                        response = hspaAppointmentService.bookedSlot(jsonNode);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseEntity<JsonNode> getError() {
        try {
            String error = "{\"message\": {\"ack\": {\"status\": \"NACK\"}},\"error\": {\"code\": \"UHI-1407\",\"message\": \"Internal server error\"}}";
            return ResponseEntity.ok(objectMapper.readTree(error));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

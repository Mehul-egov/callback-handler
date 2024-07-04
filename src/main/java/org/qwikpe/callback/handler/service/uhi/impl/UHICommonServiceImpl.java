package org.qwikpe.callback.handler.service.uhi.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.qwikpe.callback.handler.entities.uhi.CredentialsInfo;
import org.qwikpe.callback.handler.repositories.uhi.CredentialsInfoRepository;
import org.qwikpe.callback.handler.service.uhi.BloodService;
import org.qwikpe.callback.handler.service.uhi.HSPAAppointmentService;
import org.qwikpe.callback.handler.service.uhi.UHICommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UHICommonServiceImpl implements UHICommonService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BloodService bloodService;

    @Autowired
    private CredentialsInfoRepository credentialsInfoRepository;

    @Override
    public void searchResponse(String payload) {

        try {
            JsonNode jsonNode = objectMapper.readTree(payload);

            CredentialsInfo credentialsInfo = credentialsInfoRepository.findByDomainAndSubscriberId(
                    jsonNode.get("context").get("domain").asText(), jsonNode.get("context").get("consumer_id").asText()
            );

            if(credentialsInfo.getDomainName().equals("Blood Banks"))
                bloodService.setAllBloodData(jsonNode);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

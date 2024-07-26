package org.qwikpe.callback.handler.service.uhi.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.qwikpe.callback.handler.domain.uhi.CredentialsInfo;
import org.qwikpe.callback.handler.service.uhi.BloodService;
import org.qwikpe.callback.handler.service.uhi.HSPAAppointmentService;
import org.qwikpe.callback.handler.service.uhi.UHICommonService;
import org.qwikpe.callback.handler.service.uhi.UhiHeaderService;
import org.qwikpe.callback.handler.util.uhi.CredentialList;
import org.qwikpe.callback.handler.util.uhi.UhiApiResponseComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UHICommonServiceImpl implements UHICommonService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BloodService bloodService;

    @Autowired
    private CredentialList credentialList;

    @Autowired
    private HSPAAppointmentService hspaAppointmentService;

    @Autowired
    private UhiApiResponseComponent uhiApiResponseComponent;

    @Autowired
    private UhiHeaderService uhiHeaderService;

    @Override
    public void searchResponse(String payload) {

        try {
            JsonNode jsonNode = objectMapper.readTree(payload);

            CredentialsInfo credentialsInfo = credentialList.getCredentialsInfo(
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
    public ResponseEntity<JsonNode> searchRequest(String payload, HttpServletRequest httpServletRequest) {

        ResponseEntity<JsonNode> response = null;
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);

            String authorizationHeader;
            String userType;
            if(jsonNode.get("context").get("provider_uri") == null) {
                authorizationHeader = httpServletRequest.getHeader("x-gateway-authorization");
                userType = "Gateway";
            }
            else {
                authorizationHeader = httpServletRequest.getHeader("Authorization");
                userType = "EUA";
            }

            System.out.println("Header:: " + authorizationHeader);

            if(authorizationHeader != null) {
                if(uhiHeaderService.verifyHeader(authorizationHeader,payload,userType)) {

                    CredentialsInfo credentialsInfo = credentialList.getCredentialsInfo(
                            jsonNode.get("context").get("domain").asText(), jsonNode.get("context").get("provider_id").asText()
                    );

                    if(credentialsInfo == null) {
                        response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
                    } else {
                        switch(credentialsInfo.getDomainName()) {

                            case "Blood Banks" :
                                break;

                            case "TeleConsultation" :
                                response = hspaAppointmentService.searchDoctorAndSlot(jsonNode);
                        }
                    }
                } else {
                    response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerVerificationFailed());
                }
            } else {
                response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerNotFound());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseEntity<JsonNode> initRequest(String payload, HttpServletRequest httpServletRequest) {

        ResponseEntity<JsonNode> response = null;
        try {
            String authorizationHeader = httpServletRequest.getHeader("Authorization");

            if(authorizationHeader != null) {
                if(uhiHeaderService.verifyHeader(authorizationHeader,payload,"EUA")) {

                    JsonNode jsonNode = objectMapper.readTree(payload);

                    CredentialsInfo credentialsInfo = credentialList.getCredentialsInfo(
                            jsonNode.get("context").get("domain").asText(), jsonNode.get("context").get("provider_id").asText()
                    );

                    if(credentialsInfo == null) {
                        response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
                    } else {
                        switch(credentialsInfo.getDomainName()) {

                            case "Blood Banks" :
                                break;

                            case "TeleConsultation" :
                                response = hspaAppointmentService.selectSlot(jsonNode);
                        }
                    }
                } else {
                    response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerVerificationFailed());
                }
            } else {
                response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerNotFound());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseEntity<JsonNode> confirmRequest(String payload, HttpServletRequest httpServletRequest) {

        ResponseEntity<JsonNode> response = null;
        try {
            String authorizationHeader = httpServletRequest.getHeader("Authorization");

            if(authorizationHeader != null) {
                if(uhiHeaderService.verifyHeader(authorizationHeader,payload,"EUA")) {

                    JsonNode jsonNode = objectMapper.readTree(payload);
                    CredentialsInfo credentialsInfo = credentialList.getCredentialsInfo(
                            jsonNode.get("context").get("domain").asText(), jsonNode.get("context").get("provider_id").asText()
                    );

                    if(credentialsInfo == null) {
                        response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
                    } else {
                        switch(credentialsInfo.getDomainName()) {

                            case "Blood Banks" :
                                break;

                            case "TeleConsultation" :
                                response = hspaAppointmentService.bookedSlot(jsonNode);
                        }
                    }
                } else {
                    response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerVerificationFailed());
                }
            } else {
                response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerNotFound());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseEntity<JsonNode> cancelRequest(String payload, HttpServletRequest httpServletRequest) {
        ResponseEntity<JsonNode> response = null;
        try {
            String authorizationHeader = httpServletRequest.getHeader("Authorization");

            if(authorizationHeader != null) {
                if(uhiHeaderService.verifyHeader(authorizationHeader,payload,"EUA")) {

                    JsonNode jsonNode = objectMapper.readTree(payload);
                    CredentialsInfo credentialsInfo = credentialList.getCredentialsInfo(
                            jsonNode.get("context").get("domain").asText(), jsonNode.get("context").get("provider_id").asText()
                    );

                    if(credentialsInfo == null) {
                        response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
                    } else {
                        switch(credentialsInfo.getDomainName()) {

                            case "Blood Banks" :
                                break;

                            case "TeleConsultation" :
                                response = hspaAppointmentService.cancelSlot(jsonNode);
                        }
                    }
                } else {
                    response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerVerificationFailed());
                }
            } else {
                response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerNotFound());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseEntity<JsonNode> messageRequest(String payload, HttpServletRequest httpServletRequest) {
        ResponseEntity<JsonNode> response = null;
        try {
            String authorizationHeader = httpServletRequest.getHeader("Authorization");

            if(authorizationHeader != null) {
                if(uhiHeaderService.verifyHeader(authorizationHeader,payload,"EUA")) {

                    JsonNode jsonNode = objectMapper.readTree(payload);
                    response = hspaAppointmentService.sendMessage(jsonNode);
                } else {
                    response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerVerificationFailed());
                }
            } else {
                response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerNotFound());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseEntity<JsonNode> statusRequest(String payload, HttpServletRequest httpServletRequest) {
        ResponseEntity<JsonNode> response = null;
        try {
            String authorizationHeader = httpServletRequest.getHeader("Authorization");

            if(authorizationHeader != null) {
                if(uhiHeaderService.verifyHeader(authorizationHeader,payload,"EUA")) {

                    JsonNode jsonNode = objectMapper.readTree(payload);
                    CredentialsInfo credentialsInfo = credentialList.getCredentialsInfo(
                            jsonNode.get("context").get("domain").asText(), jsonNode.get("context").get("provider_id").asText()
                    );

                    if(credentialsInfo == null) {
                        response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
                    } else {
                        switch(credentialsInfo.getDomainName()) {

                            case "Blood Banks" :
                                break;

                            case "TeleConsultation" :
                                response = hspaAppointmentService.getAppointmentStatus(jsonNode);
                        }
                    }
                } else {
                    response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerVerificationFailed());
                }
            } else {
                response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerNotFound());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return response;
    }
}

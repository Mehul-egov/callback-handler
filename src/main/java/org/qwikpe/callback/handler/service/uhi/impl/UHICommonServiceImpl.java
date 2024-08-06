package org.qwikpe.callback.handler.service.uhi.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.qwikpe.callback.handler.domain.b2b.uhi.CredentialsInfo;
import org.qwikpe.callback.handler.service.uhi.*;
import org.qwikpe.callback.handler.util.Constants;
import org.qwikpe.callback.handler.util.uhi.CredentialList;
import org.qwikpe.callback.handler.util.uhi.UhiApiResponseComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Enumeration;

@Service
public class UHICommonServiceImpl implements UHICommonService {

    @Autowired
    private BloodService bloodService;

    @Autowired
    private CredentialList credentialList;

    @Autowired
    private HPRAppointmentService HPRAppointmentService;

    @Autowired
    private UhiApiResponseComponent uhiApiResponseComponent;

    @Autowired
    private UhiHeaderService uhiHeaderService;

    @Autowired
    private PHRAppointmentService phrAppointmentService;

    @Override
    public ResponseEntity<JsonNode> searchResponse(String payload, HttpServletRequest httpServletRequest) {

        try {
            JsonNode jsonNode = Constants.JACK_OBJ_MAPPER.readTree(payload);

            CredentialsInfo credentialsInfo = credentialList.getCredentialsInfo(
                    jsonNode.get("context").get("domain").asText(), jsonNode.get("context").get("consumer_id").asText()
            );

            if(credentialsInfo == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
            }

            String authorizationHeader = null;
            String userType = null;
            Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
            while (headerNames.hasMoreElements()){
                if(headerNames.nextElement().equals("x-gateway-authorization")) {
                    authorizationHeader = httpServletRequest.getHeader("x-gateway-authorization");
                    userType = "Gateway";
                }
            }
            if(authorizationHeader == null) {
                authorizationHeader = httpServletRequest.getHeader("Authorization");
                userType = "HSPA";
            }

            if(authorizationHeader == null || authorizationHeader.isEmpty()){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerNotFound());
            }

            if(!uhiHeaderService.verifyHeader(authorizationHeader,payload,userType)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerVerificationFailed());
            }

            switch(credentialsInfo.getDomainName()) {

                case "Blood Banks" :
                    if(jsonNode.get("context").get("provider_id").asText().equals("cdac.hspa")) {
                        return bloodService.setAllBloodData(jsonNode);
                    }

                case "TeleConsultation" :
                    if(userType.equals("Gateway")){
                        return phrAppointmentService.searchDoctorResponse(jsonNode);
                    }
                    else {
                        return phrAppointmentService.searchSlotResponse(jsonNode);
                    }

                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
        }
    }

    @Override
    public ResponseEntity<JsonNode> searchRequest(String payload, HttpServletRequest httpServletRequest) {

        ResponseEntity<JsonNode> response = null;
        try {
            JsonNode jsonNode = Constants.JACK_OBJ_MAPPER.readTree(payload);

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
                                response = HPRAppointmentService.searchDoctorAndSlot(jsonNode);
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

                    JsonNode jsonNode = Constants.JACK_OBJ_MAPPER.readTree(payload);

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
                                response = HPRAppointmentService.selectSlot(jsonNode);
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

                    JsonNode jsonNode = Constants.JACK_OBJ_MAPPER.readTree(payload);
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
                                response = HPRAppointmentService.bookedSlot(jsonNode);
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

                    JsonNode jsonNode = Constants.JACK_OBJ_MAPPER.readTree(payload);
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
                                response = HPRAppointmentService.cancelSlot(jsonNode);
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

                    JsonNode jsonNode = Constants.JACK_OBJ_MAPPER.readTree(payload);
                    response = HPRAppointmentService.sendMessage(jsonNode);
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

                    JsonNode jsonNode = Constants.JACK_OBJ_MAPPER.readTree(payload);
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
                                response = HPRAppointmentService.getAppointmentStatus(jsonNode);
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

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
    private HPRAppointmentService hprAppointmentService;

    @Autowired
    private UhiApiResponseComponent uhiApiResponseComponent;

    @Autowired
    private UhiHeaderService uhiHeaderService;

    @Autowired
    private PHRAppointmentService phrAppointmentService;

    @Override
    public ResponseEntity<JsonNode> phrApiResponse(String payload, HttpServletRequest httpServletRequest) {
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

            JsonNode headerVerifyResponse = this.verifyAuthorizationHeader(authorizationHeader,payload,userType);
            if(headerVerifyResponse != null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(headerVerifyResponse);
            }

            return this.sentDataCallbackToEua(credentialsInfo,jsonNode,userType);
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public ResponseEntity<JsonNode> messageResponse(String payload, HttpServletRequest httpServletRequest) {
        return null;
    }

    @Override
    public ResponseEntity<JsonNode> hprApiRequest(String payload, HttpServletRequest httpServletRequest) {
        try {
            JsonNode jsonNode = Constants.JACK_OBJ_MAPPER.readTree(payload);

            CredentialsInfo credentialsInfo = credentialList.getCredentialsInfo(
                    jsonNode.get("context").get("domain").asText(), jsonNode.get("context").get("provider_id").asText()
            );

            if(credentialsInfo == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
            }

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

            JsonNode headerVerifyResponse = this.verifyAuthorizationHeader(authorizationHeader,payload,userType);
            if(headerVerifyResponse != null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(headerVerifyResponse);
            }

            return this.sentDataCallbackToHspa(credentialsInfo,jsonNode);
        } catch(Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public ResponseEntity<JsonNode> messageRequest(String payload, HttpServletRequest httpServletRequest) {
        ResponseEntity<JsonNode> response = null;
        try {
            String authorizationHeader = httpServletRequest.getHeader("Authorization");

            if(authorizationHeader != null) {
                if(uhiHeaderService.verifyHeader(authorizationHeader,payload,"EUA")) {

                    JsonNode jsonNode = Constants.JACK_OBJ_MAPPER.readTree(payload);
                    response = hprAppointmentService.callHprTeleconsultationApi(jsonNode);
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

    private ResponseEntity<JsonNode> sentDataCallbackToHspa(
            CredentialsInfo credentialsInfo, JsonNode jsonNode) throws RuntimeException{

        ResponseEntity<JsonNode> response = null;
        switch(credentialsInfo.getDomainName()) {

            case "Blood Banks" :
                break;

            case "TeleConsultation" :
                response = hprAppointmentService.callHprTeleconsultationApi(jsonNode);
                break;

            default:
                response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerNotFound());
        }

        return response;
    }

    private ResponseEntity<JsonNode> sentDataCallbackToEua(
            CredentialsInfo credentialsInfo, JsonNode jsonNode,String userType) throws RuntimeException{

        ResponseEntity<JsonNode> response = null;
        switch(credentialsInfo.getDomainName()) {

            case "Blood Banks" :
                break;

            case "TeleConsultation" :
                response = phrAppointmentService.phrApiResponse(jsonNode,userType);
                break;

            default:
                response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.headerNotFound());
        }

        return response;
    }

    private JsonNode verifyAuthorizationHeader(String authorizationHeader,String payload,String userType) {

        if(authorizationHeader == null || authorizationHeader.isEmpty()){
            return uhiApiResponseComponent.headerNotFound();
        }

        if(!uhiHeaderService.verifyHeader(authorizationHeader,payload,userType)) {
            return uhiApiResponseComponent.headerVerificationFailed();
        }

        return null;
    }
}

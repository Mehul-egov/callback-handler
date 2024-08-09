package org.qwikpe.callback.handler.service.uhi.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.domain.b2b.uhi.CredentialsInfo;
import org.qwikpe.callback.handler.dto.uhi.LookupDto;
import org.qwikpe.callback.handler.service.uhi.UhiHeaderService;
import org.qwikpe.callback.handler.util.Common;
import org.qwikpe.callback.handler.util.Constants;
import org.qwikpe.callback.handler.util.uhi.CredentialList;
import org.qwikpe.callback.handler.util.uhi.HeaderGenerator;
import org.qwikpe.callback.handler.util.uhi.UhiWebClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UhiHeaderServiceImpl implements UhiHeaderService {

    @Autowired
    private UhiWebClientUtil webClientUtil;

    @Autowired
    private HeaderGenerator headerGenerator;

    @Autowired
    private CredentialList credentialList;

    @Override
    public boolean verifyHeader(String header, String payload, String userType) {

        try {
            JsonNode headerJson = Common.JACK_OBJ_MAPPER.readTree(header);
            JsonNode payloadJson = Common.JACK_OBJ_MAPPER.readTree(payload);

            String expires = headerJson.get("expires").asText();
            String created = headerJson.get("created").asText();
            String signature = headerJson.get("signature").asText();
            String keyId = headerJson.get("keyId").asText();
            String publicKeyId = keyId.split("\\|")[1];

            LookupDto lookupDto = new LookupDto();
            if(userType.equals("EUA")) {
                lookupDto.setSubscriber_id(payloadJson.get("context").get("consumer_id").asText());
                lookupDto.setSubscriberUrl(payloadJson.get("context").get("consumer_uri").asText());
            } else if(userType.equals("HSPA")){
                lookupDto.setSubscriber_id(payloadJson.get("context").get("provider_id").asText());
                lookupDto.setSubscriberUrl(payloadJson.get("context").get("provider_uri").asText());
            }
            else {
                lookupDto.setSubscriber_id("gateway-nha");
                lookupDto.setSubscriberUrl("https://hspasbx.abdm.gov.in/api/v1");
            }
            lookupDto.setType(userType);
            lookupDto.setDomain(payloadJson.get("context").get("domain").asText());
            lookupDto.setCountry(payloadJson.get("context").get("country").asText());
            lookupDto.setCity(payloadJson.get("context").get("city").asText());
            lookupDto.setPub_key_id(publicKeyId);

            JsonNode lookupPayloadJson = Common.JACK_OBJ_MAPPER.convertValue(lookupDto, JsonNode.class);
            CredentialsInfo credentialsInfo = credentialList.getCredentialsInfo(
                    lookupDto.getDomain(),
                    payloadJson.get("context").get("provider_id").asText()
            );

            Map<String, String> headers = new HashMap<>();
            String authorizedHeader = headerGenerator.getHeader(
                    credentialsInfo.getSubscriberId(),credentialsInfo.getPublicKeyId(),
                    credentialsInfo.getPrivateKey(),lookupPayloadJson.toString());
            headers.put("Authorization", authorizedHeader);

            JsonNode lookupResponse = webClientUtil.postMethod(Constants.UHI_BASE_URL, Constants.LOOKUP, headers, lookupPayloadJson, JsonNode.class, 1);

            if(lookupResponse.isArray()) {

                String publicKey = lookupResponse.get(0).get("encr_public_key").asText();
                return headerGenerator.verifyHeader(signature,created,expires,publicKey,payload);
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

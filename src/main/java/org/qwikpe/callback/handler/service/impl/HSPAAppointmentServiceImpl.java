package org.qwikpe.callback.handler.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.qwikpe.callback.handler.service.HSPAAppointmentService;
import org.qwikpe.callback.handler.util.Constants;
import org.qwikpe.callback.handler.util.HeaderGenerator;
import org.qwikpe.callback.handler.util.WebClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Service
public class HSPAAppointmentServiceImpl implements HSPAAppointmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HSPAAppointmentServiceImpl.class);

    private static final String subsId = "qwikpro.hspa";

    private static final String publicKeyId = "qwikpro.hspapid";

    private static final String privateKey = "MC4CAQAwBQYDK2VwBCIEIIaa7DC/8KWPbhUL/J2ycuExeOucjidoIugcuaWwKBzY";

    private static final String providerId = "qwikpro.hspa";

    private static final String providerUri = "https://abdm.qwikpe.in";

    @Autowired
    private WebClientUtil webClientUtil;

    @Autowired
    private HeaderGenerator headerGenerator;

    @Override
    public void searchDoctor(JsonNode jsonNode) {
        try{
            ObjectMapper mapper = new ObjectMapper();

            String consumerUri = jsonNode.get("context").get("consumer_uri").textValue();
            String consumerId = jsonNode.get("context").get("consumer_id").textValue();
            String messageId = jsonNode.get("context").get("message_id").textValue();
            String transactionId = jsonNode.get("context").get("transaction_id").textValue();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            String searchResponse = "{\"context\":{\"domain\":\"nic2004:85111\",\"country\":\"IND\",\"city\":\"std:011\",\"action\":\"on_search\",\"timestamp\":\""+timestamp+"\",\"core_version\":\"0.7.1\",\"consumer_id\":\""+consumerId+"\",\"consumer_uri\":\""+consumerUri+"\",\"provider_id\":\""+providerId+"\",\"provider_uri\":\""+providerUri+"\",\"transaction_id\":\""+transactionId+"\",\"message_id\":\""+messageId+"\"},\"message\":{\"catalog\":{\"descriptor\":{\"name\":\"Qwikpro\",\"images\":\"HSPA IMAGE\",\"short_desc\":\"Qwikpro Test hospital\",\"long_desc\":\"Expert institution providing patient treatment with specialized health science and auxiliary healthcare staff and extraordinary medical equipments.\"},\"providers\":[{\"id\":\"1\",\"descriptor\":{\"name\":\"Test Hospital\",\"short_desc\":\"Expertise in every field with renowned staff.\",\"long_desc\":\"We are Test hospital. We have established a very profound name in the healthcare industry by providing expert services in every healthcare fields that we have.\"},\"categories\":[{\"id\":\"1\",\"parent_category_id\":\"101\",\"descriptor\":{\"name\":\"Cardiology\",\"code\":\"CARDIOLOGY\"}},{\"id\":\"101\",\"descriptor\":{\"name\":\"Allopathy\",\"code\":\"ALLOPATHY\"}},{\"id\":\"0\",\"parent_category_id\":\"101\",\"descriptor\":{\"name\":\"General Medicine, Pharmacy, Dental surgery\",\"code\":\"GENERAL MEDICINE, PHARMACY, DENTAL SURGERY\"}}],\"fulfillments\":[{\"id\":\"0\",\"type\":\"Online\",\"agent\":{\"id\":\"virat018@hpr.abdm\",\"name\":\"Virat Kohali\",\"gender\":\"M\",\"tags\":{\"@abdm/gov/in/experience\":\"10.0\",\"@abdm/gov/in/languages\":\"Hindi, English\",\"@abdm/gov/in/education\":\"MBBS, BDS\",\"@abdm/gov/in/hpr_id\":\"73-5232-1888-8686\"}},\"start\":{\"time\":{\"timestamp\":\"2024-07-01T00:00:00\"}},\"end\":{\"time\":{\"timestamp\":\"2024-07-10T10:00:00\"}}},{\"id\":\"1\",\"type\":\"Online\",\"agent\":{\"id\":\"rohit045@hpr.ndhm\",\"name\":\"Rohit Sharma\",\"gender\":\"M\",\"tags\":{\"@abdm/gov/in/experience\":\"5.0\",\"@abdm/gov/in/languages\":\"Eng, Hin\",\"@abdm/gov/in/education\":\"MBBS\",\"@abdm/gov/in/hpr_id\":\"73-5232-1888-8686\"}},\"start\":{\"time\":{\"timestamp\":\"2024-07-01T00:00:00\"}},\"end\":{\"time\":{\"timestamp\":\"2024-07-15T00:00:00\"}}}],\"items\":[{\"id\":\"0\",\"descriptor\":{\"name\":\"Consultation\",\"code\":\"CONSULTATION\"},\"price\":{\"currency\":\"INR\",\"value\":\"500.0\"},\"category_id\":\"0\",\"fulfillment_id\":\"0\"},{\"id\":\"1\",\"descriptor\":{\"name\":\"Consultation\",\"code\":\"CONSULTATION\"},\"price\":{\"currency\":\"INR\",\"value\":\"300.0\"},\"category_id\":\"1\",\"fulfillment_id\":\"1\"}],\"location\":{\"id\":\"1\",\"descriptor\":{\"name\":\"Test Hospital\",\"short_desc\":\"Expertise in every field with renowned staff.\",\"long_desc\":\"We are Test hospital. We have established a very profound name in the healthcare industry by providing expert services in every healthcare fields that we have.\"},\"city\":{\"name\":\"Delhi\",\"code\":\"011\"},\"country\":{\"name\":\"INDIA\",\"code\":\"+91\"},\"gps\":\"18.5246036,73.792927\",\"address\":\"3rd, 7th & 9th Floor, Tower-L, Jeevan Bharati Building, Connaught Place, New Delhi, Delhi 110001\"}}]}}}";
            JsonNode responseJsonNode = mapper.readTree(searchResponse);

            Map<String, String> headers = new HashMap<>();

            String authorizedHeader = headerGenerator.getHeader(subsId,publicKeyId,privateKey,searchResponse);
            LOGGER.info("Authorization Header :: "+authorizedHeader);
            headers.put("Authorization", authorizedHeader);

            JsonNode onSearchResponse = webClientUtil.postMethod(Constants.UHI_BASE_URL, Constants.UHI_ON_SEARCH, headers, responseJsonNode, JsonNode.class, 3);

            LOGGER.info("on_Search_Response(search) :: {}", onSearchResponse);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void secondSearch(JsonNode jsonNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String consumerUri = jsonNode.get("context").get("consumer_uri").textValue();
            String consumerId = jsonNode.get("context").get("consumer_id").textValue();
            String messageId = jsonNode.get("context").get("message_id").textValue();
            String transactionId = jsonNode.get("context").get("transaction_id").textValue();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            String searchResponse = "{\"context\":{\"domain\":\"nic2004:85111\",\"country\":\"IND\",\"city\":\"std:011\",\"action\":\"on_search\",\"timestamp\":\""+timestamp+"\",\"core_version\":\"0.7.1\",\"consumer_id\":\""+consumerId+"\",\"consumer_uri\":\""+consumerUri+"\",\"provider_id\":\""+providerId+"\",\"provider_uri\":\""+providerUri+"\",\"transaction_id\":\""+transactionId+"\",\"message_id\":\""+messageId+"\"},\"message\":{\"catalog\":{\"descriptor\":{\"name\":\"Qwikpro\",\"short_desc\":\"Qwikpro Test hospital\",\"long_desc\":\"Expert institution providing patient treatment with specialized health science and auxiliary healthcare staff and extraordinary medical equipments.\"},\"providers\":[{\"id\":\"1\",\"descriptor\":{\"name\":\"Test Hospital\",\"short_desc\":\"Expertise in every field with renowned staff.\",\"long_desc\":\"We are Test hospital. We have established a very profound name in the healthcare industry by providing expert services in every healthcare fields that we have.\"},\"fulfillments\":[{\"id\":\"0933f0ca-a1c1-4ffe-bbd9-745e18c4e117\",\"type\":\"Online\",\"agent\":{\"id\":\"virat018@hpr.ndhm\",\"name\":\"Ganesh Vikram Borse\",\"gender\":\"M\",\"tags\":{\"@abdm/gov/in/experience\":\"5.0\",\"@abdm/gov/in/languages\":\"Eng, Hin\",\"@abdm/gov/in/education\":\"MBBS\",\"@abdm/gov/in/hpr_id\":\"virat018@hpr.ndhm\"}},\"start\":{\"time\":{\"timestamp\":\"2024-07-07T12:00:00\"}},\"end\":{\"time\":{\"timestamp\":\"2024-07-07T12:15:00\"}}},{\"id\":\"b23c6da9-26cd-42a7-812f-280179a8c426\",\"type\":\"Online\",\"agent\":{\"id\":\"virat018@hpr.ndhm\",\"name\":\"Ganesh Vikram Borse\",\"gender\":\"M\",\"tags\":{\"@abdm/gov/in/experience\":\"5.0\",\"@abdm/gov/in/languages\":\"Eng, Hin\",\"@abdm/gov/in/education\":\"MBBS\",\"@abdm/gov/in/hpr_id\":\"virat018@hpr.ndhm\"}},\"start\":{\"time\":{\"timestamp\":\"2024-07-08T12:15:00\"}},\"end\":{\"time\":{\"timestamp\":\"2024-07-08T12:30:00\"}}}],\"items\":[{\"id\":\"0\",\"descriptor\":{\"name\":\"Consultation\",\"code\":\"CONSULTATION\"},\"price\":{\"currency\":\"INR\",\"value\":\"0.0\"},\"fulfillment_id\":\"0933f0ca-a1c1-4ffe-bbd9-745e18c4e117\"},{\"id\":\"1\",\"descriptor\":{\"name\":\"Consultation\",\"code\":\"CONSULTATION\"},\"price\":{\"currency\":\"INR\",\"value\":\"0.0\"},\"fulfillment_id\":\"b23c6da9-26cd-42a7-812f-280179a8c426\"}],\"location\":{\"id\":\"1\",\"descriptor\":{\"name\":\"Test Hospital\",\"short_desc\":\"Expertise in every field with renowned staff.\",\"long_desc\":\"We are Test hospital. We have established a very profound name in the healthcare industry by providing expert services in every healthcare fields that we have.\"},\"city\":{\"name\":\"Delhi\",\"code\":\"011\"},\"country\":{\"name\":\"INDIA\",\"code\":\"+91\"},\"gps\":\"18.5246036,73.792927\",\"address\":\"3rd, 7th & 9th Floor, Tower-L, Jeevan Bharati Building, Connaught Place, New Delhi, Delhi 110001\"}}]}}}";
            JsonNode responseJsonNode = mapper.readTree(searchResponse);

            Map<String, String> headers = new HashMap<>();

            String authorizedHeader = headerGenerator.getHeader(subsId,publicKeyId,privateKey,jsonNode.asText());
            LOGGER.info("Authorization Header :: {}",authorizedHeader);
            headers.put("Authorization", authorizedHeader);

            JsonNode onSearchResponse = webClientUtil.postMethod(consumerUri, Constants.UHI_ON_SEARCH, headers, responseJsonNode, JsonNode.class, 3);

            LOGGER.info("on_Search_Response(Second search) :: {}", onSearchResponse);

        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}

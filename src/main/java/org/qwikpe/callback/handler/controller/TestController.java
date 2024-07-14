package org.qwikpe.callback.handler.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.tomcat.util.bcel.Const;
import org.qwikpe.callback.handler.dto.CareContextDTORef;
import org.qwikpe.callback.handler.service.uhi.BloodService;
import org.qwikpe.callback.handler.util.Constants;
import org.qwikpe.callback.handler.util.URIMapping;
import org.qwikpe.callback.handler.util.WebClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Condition;

@RestController
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private WebClientUtil webClientUtil;

    @GetMapping("/v0.5/test")
    public ResponseEntity<Object> test() {
        LOGGER.info("Welcome to test api of callback handler !!");
        return ResponseEntity.ok("Welcome to test api of callback handler !!");
    }

    @PostMapping(value = "/v0.5/care-contexts/discover")
    public void v05CareContextDiscover(@RequestBody Map<String, Object> requestBody) {
        LOGGER.info("v05CareContextDiscover :: requestBody: {}", requestBody);
        try {
            CareContextDTORef.CareContextDTO careContext = new CareContextDTORef.CareContextDTO();

            careContext.setTransactionId(requestBody.get("transactionId").toString());

            CareContextDTORef.Patient patient = new CareContextDTORef.Patient();
            patient.setReferenceNumber("Nirshad H Sheikh");
            patient.setDisplay("Apollo_Encounter_123_2023070414zz");
            patient.setHiType("Prescription");
            patient.setCount(1);

            CareContextDTORef.CareContext careContext1 = new CareContextDTORef.CareContext();
            careContext1.setReferenceNumber("duplication-testing-care-context-5");
            careContext1.setDisplay("Testing the duplication");

            patient.setCareContexts(List.of(careContext1));

            CareContextDTORef.Patient patient2 = new CareContextDTORef.Patient();
            patient.setReferenceNumber("Mayuri C");
            patient.setDisplay("Apollo_Encounter_123_2023070414zz");
            patient.setHiType("WellnessRecord");
            patient.setCount(1);

            CareContextDTORef.CareContext careContext2 = new CareContextDTORef.CareContext();
            careContext2.setReferenceNumber("duplication-testing-care-context-4");
            careContext2.setDisplay("blood test testing");

            patient2.setCareContexts(List.of(careContext2));

            careContext.setPatient(List.of(patient2, patient));
            careContext.setMatchedBy(List.of("MR"));
            careContext.setResponse(new CareContextDTORef.
                    Response(requestBody.get("requestId").toString()));

            OffsetDateTime currentUTCTimeStamp = OffsetDateTime.now(ZoneOffset.UTC);
            DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendInstant(3).toFormatter();
            String isoUtcString = currentUTCTimeStamp.format(formatter);

            Map<String, String> headers = new HashMap<>() {{
                put(Constants.REQUEST_ID, UUID.randomUUID().toString());
                put(Constants.TIMESTAMP, isoUtcString);
                put(Constants.X_CM_ID, "sbx");
            }};

            JsonNode jsonNode = webClientUtil.postMethod(URIMapping.ABDM_BASE_URL, "/api/v3/hiecm/user-initiated-linking/patient/care-context/on-discover"
                    , headers, careContext, JsonNode.class, Constants.MAX_RETRY);

            LOGGER.info("on_Discover_Response(v0.5) :: {}", jsonNode);

        } catch (Exception e) {
            LOGGER.error("onDiscoverCareContext :: Error while processing the request body of v05CareContextDiscover: {}", requestBody, e);
        }
    }
}

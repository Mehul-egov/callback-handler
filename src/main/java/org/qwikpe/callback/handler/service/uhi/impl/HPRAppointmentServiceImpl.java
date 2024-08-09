package org.qwikpe.callback.handler.service.uhi.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.enums.Action;
import org.qwikpe.callback.handler.service.uhi.HPRAppointmentService;
import org.qwikpe.callback.handler.util.Constants;
import org.qwikpe.callback.handler.util.WebClientUtil;
import org.qwikpe.callback.handler.util.uhi.UhiApiResponseComponent;
import org.qwikpe.callback.handler.util.uhi.UhiWebClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HPRAppointmentServiceImpl implements HPRAppointmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HPRAppointmentServiceImpl.class);

    @Autowired
    private UhiWebClientUtil webClientUtil;

    @Autowired
    private UhiApiResponseComponent uhiApiResponseComponent;

    @Override
    public ResponseEntity<JsonNode> callHprTeleconsultationApi(JsonNode jsonNode) {
        try {
            Map<String, String> headers = new HashMap<>();
            Action action = Action.getAction(jsonNode.get("context").get("action").asText());
            String apiUri = null;

            switch (action) {
                case search:
                    if(jsonNode.get("context").get("provider_uri") == null)
                        apiUri = Constants.SEARCH_DOCTOR;
                    else
                        apiUri = Constants.SEARCH_SLOT;
                    break;

                case init:
                    apiUri = Constants.SELECT_SLOT;
                    break;

                case confirm:
                    apiUri = Constants.BOOK_SLOT;
                    break;

                case cancel:
                    apiUri = Constants.CANCEL_SLOT;
                    break;

                case status:
                apiUri = Constants.STATUS;
                    break;

                case on_message:
                    apiUri = Constants.MESSAGE;
                    break;

                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
            }

            JsonNode response =
                    webClientUtil.postMethod(
                            Constants.APPOINTMENT_BASE_URI,
                            apiUri,
                            headers,
                            jsonNode,
                            JsonNode.class,
                            1);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}

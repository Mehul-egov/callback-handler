package org.qwikpe.callback.handler.service.uhi.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.enums.Action;
import org.qwikpe.callback.handler.service.uhi.PHRAppointmentService;
import org.qwikpe.callback.handler.util.Constants;
import org.qwikpe.callback.handler.util.uhi.UhiApiResponseComponent;
import org.qwikpe.callback.handler.util.uhi.UhiWebClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PHRAppointmentServiceImpl implements PHRAppointmentService {

    @Autowired
    private UhiWebClientUtil webClientUtil;

    @Autowired
    private UhiApiResponseComponent uhiApiResponseComponent;

    @Override
    public ResponseEntity<JsonNode> phrApiResponse(JsonNode responseJsonNode,String requestedBy) {
        try{
            Map<String, String> headers = new HashMap<>();
            Action action = Action.getAction(responseJsonNode.get("context").get("action").asText());
            String apiUri = null;

            if(requestedBy.equals("Gateway") && action.equals(Action.on_search)) {
                apiUri = Constants.DOCTOR_SEARCH_RESPONSE;
            } else {
                switch (action) {
                    case on_search:
                        apiUri = Constants.SLOT_SEARCH_RESPONSE;
                        break;
                    case on_init:
                        apiUri = Constants.INIT_RESPONSE;
                        break;

                    case on_confirm:
                        apiUri = Constants.CONFIRM_RESPONSE;
                        break;

                    case on_cancel:
                        apiUri = Constants.CANCEL_RESPONSE;
                        break;

                    case on_status:
                        apiUri = Constants.STATUS_RESPONSE;
                        break;

                    case on_message:
                        apiUri = Constants.MESSAGE_RESPONSE;
                        break;

                    default:
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uhiApiResponseComponent.internalServerError());
                }
            }

            JsonNode response = webClientUtil
                    .postMethod(Constants.PHR_APPOINTMENT_BASE_URI, apiUri, headers, responseJsonNode, JsonNode.class, 1);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}

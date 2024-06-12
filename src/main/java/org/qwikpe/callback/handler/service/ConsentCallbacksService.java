package org.qwikpe.callback.handler.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.Objects;

public interface ConsentCallbacksService {
    void consentRequestOnInit(JsonNode payload);
}

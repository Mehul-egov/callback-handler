package org.qwikpe.callback.handler.service.hip;

import java.util.Map;

public interface HipCareContextCallbacksService {
    void onCareContext(Map<String, Object> requestBody);

    void discoverCareContext(Map<String, Object> requestBody, String HipId);

    void initCareContext(Map<String, Object> requestBody);
}

package org.qwikpe.callback.handler.util.uhi;

import java.util.Map;

public interface UhiWebClientUtil {
    <T> T postMethod(String baseUrl,
                     String uri, Map<String, String> headers, Object body, Class<T> type, int maxRetryCount);
}

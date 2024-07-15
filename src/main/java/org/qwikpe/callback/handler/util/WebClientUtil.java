package org.qwikpe.callback.handler.util;

import java.util.Map;

public interface WebClientUtil {
    <T> T postMethod(String baseUrl,
                     String uri, Map<String, String> headers, Object body, Class<T> type, int maxRetryCount);
}

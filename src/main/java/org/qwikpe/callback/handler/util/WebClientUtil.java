package org.qwikpe.callback.handler.util;

import org.springframework.util.MultiValueMap;

import java.util.Map;

public interface WebClientUtil {
    <T> T postMethod(String baseUrl,
                     String uri, Map<String, String> headers, Object body, Class<T> type, int maxRetryCount, MultiValueMap<String, String> param);

    <T> T postMethod(String baserUrl,
                     Map<String, String> headers, Object body, Class<T> type,
                     int maxRetryCount);
}

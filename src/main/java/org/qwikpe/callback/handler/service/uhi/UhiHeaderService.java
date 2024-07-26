package org.qwikpe.callback.handler.service.uhi;

public interface UhiHeaderService {
    boolean verifyHeader(String header, String payload, String userType);
}

package org.qwikpe.callback.handler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CareContextDTORef {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class CareContextDTO{
        private String transactionId;
        private List<Patient> patient;
        private List<String> matchedBy;
        private Response response;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Patient {
        private String referenceNumber;
        private String display;
        private List<CareContext> careContexts;
        private String hiType;
        private Integer count;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class CareContext {
        private String referenceNumber;
        private String display;

    }
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private String requestId;

    }
}

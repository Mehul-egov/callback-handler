package org.qwikpe.callback.handler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class HipRequestDTO {

    private String requestId;
    private String timestamp;
    private String transactionId;
    private HiRequest hiRequest;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class HiRequest {

        private Consent consent;
        private DateRange dateRange;
        private String dataPushUrl;
        private KeyMaterial keyMaterial;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class Consent {
        private String id;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class DateRange {
        private String from;
        private String to;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class KeyMaterial {
        private String cryptoAlg;
        private String curve;
        private DhPublicKey dhPublicKey;
        private String nonce;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class DhPublicKey {
        private String expiry;
        private String parameters;
        private String keyValue;
    }
}

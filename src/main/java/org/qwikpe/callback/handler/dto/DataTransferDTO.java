package org.qwikpe.callback.handler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class DataTransferDTO {
    private int pageNumber;
    private int pageCount;
    private String transactionId;
    private List<Entry> entries;
    private KeyMaterial keyMaterial;

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    public static class Entry {
        private String content;
        private String media;
        private String checksum;
        private String careContextReference;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    public static class KeyMaterial {
        private String cryptoAlg;
        private String curve;
        private DhPublicKey dhPublicKey;
        private String nonce;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    public static class DhPublicKey {
        private String expiry;
        private String parameters;
        private String keyValue;
    }
}

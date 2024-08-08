package org.qwikpe.callback.handler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class ConsentRequestHipNotifyDTO {

    private Notification notification;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class Notification{

        private String status;
        private String consentId;
        private ConsentDetail consentDetail;
        private String signature;
        private boolean grantAcknowledgement;
    }


    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class ConsentDetail {

        private String schemaVersion;
        private String consentId;
        private String createdAt;
        private Patient patient;
        private List<CareContext> careContexts;
        private Purpose purpose;
        private Hip hip;
        private ConsentManager consentManager;
        private List<String> hiTypes;
        private Permission permission;

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        @ToString
        public static class Patient {
            private String id;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        @ToString
        public static class CareContext {
            private String patientReference;
            private String careContextReference;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        @ToString
        public static class Purpose {
            private String text;
            private String code;
            private String refUri;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        @ToString
        public static class Hip {
            private String id;
            private String name;

        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        @ToString
        public static class ConsentManager {
            private String id;

        }

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        @ToString
        public static class Permission {
            private String accessMode;
            private DateRange dateRange;
            private String dataEraseAt;
            private Frequency frequency;

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
            public static class Frequency {
                private String unit;
                private int value;
                private int repeats;
            }
        }
    }
}

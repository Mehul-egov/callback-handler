package org.qwikpe.callback.handler.dto.uhi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseMessageDto {

    private Message message;
    private Error error;

    public ApiResponseMessageDto(String ackStatus) {
        this.message = new Message(ackStatus);
    }

    public ApiResponseMessageDto(String ackStatus, String errorCode, String errorMessage) {
        this.message = new Message(ackStatus);
        this.error = new Error(errorCode,errorMessage);
    }

    @Getter
    @NoArgsConstructor
    private static class Message {
        private Map<String,String> ack;

        public Message(String status) {
            this.ack = new HashMap<>();
            this.ack.put("status",status);
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Error {
        private String code;
        private String message;
    }
}

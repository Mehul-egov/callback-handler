package org.qwikpe.callback.handler.util.uhi;

import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.dto.uhi.ApiResponseMessageDto;
import org.qwikpe.callback.handler.util.Constants;
import org.springframework.stereotype.Component;

@Component
public class UhiApiResponseComponent {

    public JsonNode successResponse() {
        ApiResponseMessageDto apiResponseMessageDto = new ApiResponseMessageDto("ACK");
        return Constants.JACK_OBJ_MAPPER.convertValue(apiResponseMessageDto, JsonNode.class);
    }

    public JsonNode internalServerError() {
        ApiResponseMessageDto apiResponseMessageDto = new ApiResponseMessageDto("NACK","UHI-1407","Internal Server Error");
        return Constants.JACK_OBJ_MAPPER.convertValue(apiResponseMessageDto, JsonNode.class);
    }

    public JsonNode headerVerificationFailed() {
        ApiResponseMessageDto apiResponseMessageDto = new ApiResponseMessageDto("NACK","UHI-1407","Header Verification Failed");
        return Constants.JACK_OBJ_MAPPER.convertValue(apiResponseMessageDto, JsonNode.class);
    }

    public JsonNode headerNotFound() {
        ApiResponseMessageDto apiResponseMessageDto = new ApiResponseMessageDto("NACK","UHI-1407","Header Not Found");
        return Constants.JACK_OBJ_MAPPER.convertValue(apiResponseMessageDto, JsonNode.class);
    }
}

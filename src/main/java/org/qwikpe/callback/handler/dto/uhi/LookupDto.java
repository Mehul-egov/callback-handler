package org.qwikpe.callback.handler.dto.uhi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LookupDto {
    private String subscriber_id;
    private String type;
    private String domain;
    private String country;
    private String city;
    private String pub_key_id;
    private String subscriberUrl;
}

package org.qwikpe.callback.handler.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbdmTrackingDTO {
    private String consentId;

    private String qwikpeRequestId;

    private String abdmRequestId;

    private String transactionId;

    private String actionType;

    private String qwikpeUserId;

    private String qwikpeUserType;

    private String abdmIdentifier;

    private String qwikpeFacilityId;

    private String abdmIdentifierType;

    private String abdmFacilityId;

    private String abdmFacilityType;

    private String trackingDetails;

}
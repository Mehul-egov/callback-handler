package org.qwikpe.callback.handler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicalRecordCommonDetailsDTO {
    Long id;

    String patientName;

    String patientReference;

    String patientGender;

    String patientBirthDate;

    String practitionerName;

    String practitionerId;

    String facilityName;

    String facilityId;

    String encounter;

    String visitDate;

    String careContextReference;

}

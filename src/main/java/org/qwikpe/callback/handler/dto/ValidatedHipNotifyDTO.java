package org.qwikpe.callback.handler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ValidatedHipNotifyDTO {

    private String abhaAddress;
    private String qwikpeFacilityId;
    private List<MedicalRecordDTO> medicalRecordDTOList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @ToString
    public static class MedicalRecordDTO {
        @JsonProperty("medical_record_id")
        private Long medicalRecordId;

        @JsonProperty("medical_record_type")
        private String medicalRecordType;

        @JsonProperty("patient_record_id")
        private Integer patientReferenceId;

        @JsonProperty("care_context_id")
        private Integer careContextId;

        @JsonProperty("patient_reference_number")
        private String patientReferenceNumber;

        @JsonProperty("care_context_reference_number")
        private String careContextReferenceNumber;

        @JsonProperty("hpr_details_id")
        private String hprDetailsId;

        @JsonProperty("patient_details_id")
        private String patientDetailsId;

        @JsonProperty("medical_record_common_detail_id")
        private Long medicalRecordCommonDetailId;
    }
}

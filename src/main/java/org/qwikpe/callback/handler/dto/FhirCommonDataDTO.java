package org.qwikpe.callback.handler.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FhirCommonDataDTO {

    @NotBlank(message = "bundleType is mandatory, and should be from PrescriptionRecord, OPConsultRecord, HealthDocumentRecord, DiagnosticReportRecord, DischargeSummaryRecord, WellnessRecord")
    private String bundleType;

    @NotBlank(message = "careContextReference is mandatory and must not be empty")
    private String careContextReference;

    private String authoredOn;

    private String visitDate;

    private String encounter;

    @Valid
    @NotNull(message = "Patient demographic details are mandatory and must not be empty")
    private PatientResource patient;

    @Valid
    @NotNull(message = "practitioners are mandatory and must not be empty")
    private List<PractitionerResource> practitioners;

    @Valid
    @NotNull(message = "organisation is mandatory")
    private OrganisationResource organisation;

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ToString
    public static class PatientResource {
        private String name;

        private String patientReference;

        private String gender;

        private String birthDate;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ToString
    public static class PractitionerResource {
        private String name;

        private String practitionerId;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ToString
    public static class OrganisationResource {
        private String facilityName;

        private String facilityId;
    }

}

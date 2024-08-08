package org.qwikpe.callback.handler.domain.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.qwikpe.callback.handler.domain.patient.medicalrecord.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientDocument {

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

    @Valid
    private PrescriptionRecord prescriptionRecord;

    @Valid
    private OPConsultationRecord opConsultationRecord;

    @Valid
    private HealthDocumentRecord healthDocumentRecord;

    @Valid
    private DiagnosticReportRecord diagnosticReportRecord;

    @Valid
    private DischargeSummaryRecord dischargeSummaryRecord;

    @Valid
    private WellnessRecord wellnessRecord;

    @Valid
    private DocumentResource documentResource;

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
    public static class OrganisationResource {
        private String facilityName;

        private String facilityId;
    }
}

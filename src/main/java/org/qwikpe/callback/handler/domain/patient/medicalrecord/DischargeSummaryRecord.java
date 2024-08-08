package org.qwikpe.callback.handler.domain.patient.medicalrecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.qwikpe.callback.handler.helper.medicalrecord.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class DischargeSummaryRecord {

    @Valid
    private List<ChiefComplaintResource> chiefComplaints;
    @Valid private List<ObservationResource> physicalExaminations;
    private List<String> allergies;
    @Valid private List<ChiefComplaintResource> medicalHistories;
    @Valid private List<FamilyObservationResource> familyHistories;
    @Valid private List<DiagnosticResource> diagnostics;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}(T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z)?$",
            message = "Value must match either yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @NotNull(message = "authoredOn is mandatory timestamp") @NotNull private String authoredOn;

    @Valid private List<PrescriptionResource> medications;
    @Valid private List<ProcedureResource> procedures;
    @Valid private List<DocumentResource> documents;
}

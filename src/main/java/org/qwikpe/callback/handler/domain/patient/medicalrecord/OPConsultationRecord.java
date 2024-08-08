package org.qwikpe.callback.handler.domain.patient.medicalrecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import lombok.*;
import org.qwikpe.callback.handler.helper.medicalrecord.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class OPConsultationRecord {


    @Valid
    private List<ChiefComplaintResource> chiefComplaints;
    @Valid
    private List<ObservationResource> physicalExaminations;
    private List<String> allergies;
    @Valid
    private List<ChiefComplaintResource> medicalHistories;
    @Valid
    private List<FamilyObservationResource> familyHistories;
    @Valid
    private List<ServiceRequestResource> serviceRequests;
    @Valid
    private List<PrescriptionResource> medications;
    @Valid
    private List<FollowupResource> followups;
    @Valid
    private List<ProcedureResource> procedures;
    @Valid
    private List<ServiceRequestResource> referrals;
    @Valid
    private List<ObservationResource> otherObservations;
    @Valid
    private List<DocumentResource> documents;

}


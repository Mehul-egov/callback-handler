package org.qwikpe.callback.handler.domain.patient.medicalrecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import lombok.*;
import org.qwikpe.callback.handler.helper.medicalrecord.ObservationResource;
import org.qwikpe.callback.handler.helper.medicalrecord.WellnessObservationResource;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class WellnessRecord {

    @Valid
    private List<WellnessObservationResource> vitalSigns;

    @Valid
    private List<WellnessObservationResource> bodyMeasurements;

    @Valid
    private List<WellnessObservationResource> physicalActivities;

    @Valid
    private List<WellnessObservationResource> generalAssessments;

    @Valid
    private List<WellnessObservationResource> womanHealths;

    @Valid
    private List<WellnessObservationResource> lifeStyles;

    @Valid
    private List<ObservationResource> otherObservations;

    @Valid
    private List<DocumentResource> documents;
}

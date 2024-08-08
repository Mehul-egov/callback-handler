package org.qwikpe.callback.handler.domain.patient.medicalrecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.qwikpe.callback.handler.helper.medicalrecord.ImmunizationResource;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class ImmunizationRecord {


    @Valid
    @NotNull(message = "Immunizations are mandatory")
    private List<ImmunizationResource> immunizations;

    @Valid private List<DocumentResource> documents;
}

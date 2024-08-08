package org.qwikpe.callback.handler.domain.patient.medicalrecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class HealthDocumentRecord {

    @NotNull(message = "documents are mandatory") @Valid
    private List<DocumentResource> documents;
}

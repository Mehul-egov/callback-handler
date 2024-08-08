package org.qwikpe.callback.handler.domain.patient.medicalrecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import lombok.*;
import org.qwikpe.callback.handler.helper.medicalrecord.DiagnosticResource;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class DiagnosticReportRecord {

    @Valid
    private List<DiagnosticResource> diagnostics;

    @Valid
    private List<DocumentResource> documents;
}

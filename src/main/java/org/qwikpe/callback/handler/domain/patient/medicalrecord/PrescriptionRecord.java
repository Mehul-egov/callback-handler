package org.qwikpe.callback.handler.domain.patient.medicalrecord;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class PrescriptionRecord {

    private List<Prescription> prescriptions;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    public static class Prescription {
        @NotBlank(message = "medicine is mandatory")
        private String medicine;

        @NotBlank(message = "dosage is mandatory")
        private String dosage;

        @Pattern(
                regexp = "^\\d{1,2}-\\d{1,2}-(S|MIN|H|D|WK|MO)$",
                message =
                        "timing should have frequency-period-periodUnit '(0-99)-(0-99)-(S ~ seconds | MIN ~ minutes | H ~ hours | D ~ days| WK ~ week| MO ~ month' ex: 1-2-D")
        private String timing;

        private String route;
        private String method;
        private String additionalInstructions;
        private String reason;
    }
}

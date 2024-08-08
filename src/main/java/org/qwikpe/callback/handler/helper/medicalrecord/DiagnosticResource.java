/* (C) 2024 */
package org.qwikpe.callback.handler.helper.medicalrecord;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DiagnosticResource {
  @NotBlank(message = "serviceName is mandatory")
  private String serviceName;

  @NotBlank(message = "serviceCategory is mandatory")
  private String serviceCategory;

  @Valid
  @NotNull(message = "results of the report is mandatory")
  private List<ObservationResource> result;

  @NotBlank(message = "conclusion is mandatory")
  private String conclusion;

  @Valid
  private DiagnosticPresentedForm presentedForm;
}

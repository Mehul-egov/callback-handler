/* (C) 2024 */
package org.qwikpe.callback.handler.helper.medicalrecord;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DiagnosticPresentedForm {
  @NotBlank(message = "presentedForm contentType is mandatory")
  private String contentType;

  @NotBlank(message = "presentedForm data is mandatory")
  private String data;
}

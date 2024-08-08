/* (C) 2024 */
package org.qwikpe.callback.handler.helper.medicalrecord;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ObservationResource {
  @NotBlank(message = "observation is mandatory")
  private String observation;

  //  @NotNull(message = "result is mandatory")
  private String result;

  private ValueQuantityResource valueQuantity;
}

/* (C) 2024 */
package org.qwikpe.callback.handler.helper.medicalrecord;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ChiefComplaintResource {
  @NotBlank(message = "complaint is mandatory")
  private String complaint;

  @NotBlank(message = "recordedDate is mandatory")
  private String recordedDate;

  private DateRange dateRange;
}

/* (C) 2024 */
package org.qwikpe.callback.handler.helper.medicalrecord;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ServiceRequestResource {
  @NotBlank(message = "status is mandatory")
  private String status;

  @NotBlank(message = "details of service is mandatory")
  private String details;

  private String specimen;
}

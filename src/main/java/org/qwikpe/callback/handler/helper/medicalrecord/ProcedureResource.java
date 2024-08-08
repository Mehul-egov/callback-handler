/* (C) 2024 */
package org.qwikpe.callback.handler.helper.medicalrecord;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ProcedureResource {
  @NotBlank(message = "date of the procedure is mandatory")
  private String date;

  @Pattern(regexp = "COMPLETED|INPROGRESS")
  private String status;

  @NotBlank(message = "procedureReason is mandatory")
  private String procedureReason;

  private String outcome;

  @NotBlank(message = "procedureName is mandatory")
  private String procedureName;
}

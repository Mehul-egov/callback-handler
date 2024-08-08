/* (C) 2024 */
package org.qwikpe.callback.handler.helper.medicalrecord;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ImmunizationResource {
  //  @Pattern(
  //      regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
  //      message = "Date must be in the format yyyy-MM-dd")
  @NotBlank(message = "date of vaccine is mandatory")
  private String date;

  @NotBlank(message = "vaccineName is mandatory")
  private String vaccineName;

  private String lotNumber;
  private String manufacturer;
  private int doseNumber;
}

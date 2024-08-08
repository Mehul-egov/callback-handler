/* (C) 2024 */
package org.qwikpe.callback.handler.domain.patient.medicalrecord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class DocumentResource {
  @NotBlank(message = "contentType is mandatory")
  private String contentType;

  @NotBlank(message = "type is mandatory")
  private String type;

  @NotNull(message = "data is mandatory") private byte[] data;
}

/* (C) 2024 */
package org.qwikpe.callback.handler.helper.medicalrecord;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ValueQuantityResource {
  private String unit;
  private double value;
}

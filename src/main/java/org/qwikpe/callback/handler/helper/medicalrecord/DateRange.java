/* (C) 2024 */
package org.qwikpe.callback.handler.helper.medicalrecord;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateRange {
  private String from;
  private String to;
}

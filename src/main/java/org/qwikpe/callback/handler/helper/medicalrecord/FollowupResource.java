/* (C) 2024 */
package org.qwikpe.callback.handler.helper.medicalrecord;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class FollowupResource {
  private String serviceType;
  private String appointmentTime;
  private String reason;
}

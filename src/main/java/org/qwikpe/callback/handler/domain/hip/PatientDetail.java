package org.qwikpe.callback.handler.domain.hip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@Entity
@Table(schema = "patient", name = "patient_details")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientDetail {

    @Id
    private String id;

    private String healthId;

    private String linkToken;

    private String abhaNumber;

    private String facilityId;

    private String name;

    private String gender;

    private Timestamp createdTime;

    private Timestamp updatedTime;
}
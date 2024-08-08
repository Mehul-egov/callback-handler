package org.qwikpe.callback.handler.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.qwikpe.callback.handler.dto.MedicalRecordCommonDetailsDTO;
import org.qwikpe.callback.handler.dto.ValidatedHipNotifyDTO;
import org.qwikpe.callback.handler.util.Common;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
@DependsOn("common")
public class CustomQueries {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public ValidatedHipNotifyDTO findByAbhaAddressAndCareContextAndPatientReferenceAndHiTypes(String abhaAddress, Set<String> patientReferenceSet, Set<String> careContextSet, List<String> recordType) throws IOException {
        String sql = """
                 SELECT pd.health_id AS abha_address,
                 pd.facility_id AS qwikpe_facility_id,
                 JSON_ARRAYAGG( JSON_BUILD_OBJECT(
                 'medical_record_id', mr.id,
                 'medical_record_type', mr.type,
                 'patient_reference_id', pr.id,
                 'care_context_id', cc.id,
                 'patient_reference_number', pr.reference_number,
                 'care_context_reference_number', cc.reference_number,
                 'patient_details_id', mr.patient_details_id,
                 'hpr_details_id', mr.hpr_details_id,
                 'medical_record_common_detail_id', mr.medical_record_common_detail_id)
                 ) AS medical_records
                 FROM patient.patient_details AS pd
                 JOIN patient.patient_reference AS pr ON pr.patient_details_id = pd.id
                 JOIN patient.care_contexts AS cc ON cc.patient_reference_id = pr.id
                 JOIN patient.medical_records AS mr ON mr.care_context_id = cc.id
                 WHERE pd.health_id = :abhaAddress AND
                 pr.reference_number IN (:patientReferenceSet) AND
                 cc.reference_number IN (:careContextSet) AND
                 mr.type IN (:recordType)
                 GROUP BY pd.health_id, pd.facility_id
                """;

        List<Object[]> results = entityManager.createNativeQuery(sql)
                .setParameter("abhaAddress", abhaAddress)
                .setParameter("patientReferenceSet", patientReferenceSet)
                .setParameter("careContextSet", careContextSet)
                .setParameter("recordType", recordType)
                .getResultList();

        if (!results.isEmpty()) {
            Object[] result = results.get(0);

            ValidatedHipNotifyDTO validatedHipNotifyDTO = new ValidatedHipNotifyDTO();
            validatedHipNotifyDTO.setAbhaAddress((String) result[0]);
            validatedHipNotifyDTO.setQwikpeFacilityId((String) result[1]);
            JsonNode jsonNode = Common.JACK_OBJ_MAPPER.readTree((String) result[2]);

            List<ValidatedHipNotifyDTO.MedicalRecordDTO> medicalRecordDTOList = new ArrayList<>();
            for (JsonNode node : jsonNode) {
                ValidatedHipNotifyDTO.MedicalRecordDTO medicalRecordDTO = new ValidatedHipNotifyDTO.MedicalRecordDTO();
                medicalRecordDTO.setMedicalRecordId(node.get("medical_record_id").asLong());
                medicalRecordDTO.setMedicalRecordType(node.get("medical_record_type").asText());
                medicalRecordDTO.setPatientReferenceId(node.get("patient_reference_id").asInt());
                medicalRecordDTO.setCareContextId(node.get("care_context_id").asInt());
                medicalRecordDTO.setPatientReferenceNumber(node.get("patient_reference_number").asText());
                medicalRecordDTO.setCareContextReferenceNumber(node.get("care_context_reference_number").asText());
                medicalRecordDTO.setPatientDetailsId(node.get("patient_details_id").asText());
                medicalRecordDTO.setHprDetailsId(node.get("hpr_details_id").asText());
                medicalRecordDTO.setMedicalRecordCommonDetailId(node.get("medical_record_common_detail_id").asLong());
                medicalRecordDTOList.add(medicalRecordDTO);
            }

            validatedHipNotifyDTO.setMedicalRecordDTOList(medicalRecordDTOList);

            return validatedHipNotifyDTO;
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<ValidatedHipNotifyDTO.MedicalRecordDTO> findMedicalRecordToSendByConsentId(String consentId) throws JsonProcessingException {

        String sql = """
                 SELECT id, tracking_details
                 FROM tracking.abdm_tracking
                 WHERE consent_id = :value1
                """;

        List<Object[]> results = entityManager.createNativeQuery(sql).setParameter("value1", consentId).getResultList();

        if (results.isEmpty()) {
            throw new RuntimeException(("No Record found for request"));
        } else if (results.size() > 1) {
            throw new RuntimeException("More than one record found for request");
        }

        Object[] result = results.get(0);
        JsonNode jsonNode = Common.JACK_OBJ_MAPPER.readValue((String) result[1],
                JsonNode.class);

        return Common.JACK_OBJ_MAPPER.convertValue(jsonNode, new TypeReference<>() {
        });
    }

    public List<MedicalRecordCommonDetailsDTO> findAllByMedicalRecordCommonDetailsId(Collection<Long> medicalRecordCommonDetailIdList) {
        String sql = """
                SELECT mrcd.id AS id,
                mrcd.patient_name AS patientName,
                mrcd.patient_reference AS patientReference,
                mrcd.patient_gender AS patientGender,
                mrcd.patient_birth_date AS patientBirthDate,
                mrcd.practitioner_id AS practitionerId,
                mrcd.facility_name AS facilityName,
                mrcd.facility_id AS facilityId,
                mrcd.encounter AS encounter,
                mrcd.visit_date AS visitDate,
                mrcd.care_context_reference AS careContextReference,
                mrcd.practitioner_name As practitionerName
                FROM patient.medical_record_common_details AS mrcd
                WHERE mrcd.id IN (:value1)
                """;

        List<Object[]> medicalRecordCommonDetails =
                entityManager.createNativeQuery(sql).setParameter("value1", medicalRecordCommonDetailIdList).getResultList();

        List<MedicalRecordCommonDetailsDTO> medicalRecordCommonDetailsDTOList = new ArrayList<>();
        for (Object[] medicalRecordCommonDetail : medicalRecordCommonDetails) {
            MedicalRecordCommonDetailsDTO medicalRecordCommonDetailsDTO =
                    MedicalRecordCommonDetailsDTO.builder()
                            .id(Long.valueOf(medicalRecordCommonDetail[0].toString()))
                            .patientName(medicalRecordCommonDetail[1].toString())
                            .patientReference(medicalRecordCommonDetail[2].toString())
                            .patientGender(medicalRecordCommonDetail[3].toString())
                            .patientBirthDate(medicalRecordCommonDetail[4].toString())
                            .practitionerId(medicalRecordCommonDetail[5].toString())
                            .facilityName(medicalRecordCommonDetail[6].toString())
                            .facilityId(medicalRecordCommonDetail[7].toString())
                            .encounter(medicalRecordCommonDetail[8].toString())
                            .visitDate(medicalRecordCommonDetail[9].toString())
                            .careContextReference(medicalRecordCommonDetail[10].toString())
                            .practitionerName(medicalRecordCommonDetail[11].toString())
                            .build();

            medicalRecordCommonDetailsDTOList.add(medicalRecordCommonDetailsDTO);
        }

        return medicalRecordCommonDetailsDTOList;
    }

}

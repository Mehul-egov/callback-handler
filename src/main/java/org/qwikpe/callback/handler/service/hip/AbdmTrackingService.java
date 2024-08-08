package org.qwikpe.callback.handler.service.hip;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.qwikpe.callback.handler.dto.ValidatedHipNotifyDTO;

import java.util.List;

public interface AbdmTrackingService {
    List<ValidatedHipNotifyDTO.MedicalRecordDTO> findMedicalRecordToSendByConsentId(String consentId) throws JsonProcessingException;
}

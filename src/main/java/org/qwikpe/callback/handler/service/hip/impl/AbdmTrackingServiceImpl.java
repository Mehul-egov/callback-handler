package org.qwikpe.callback.handler.service.hip.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.qwikpe.callback.handler.dto.ValidatedHipNotifyDTO;
import org.qwikpe.callback.handler.service.hip.AbdmTrackingService;
import org.qwikpe.callback.handler.sql.CustomQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AbdmTrackingServiceImpl implements AbdmTrackingService {

    @Autowired
    private CustomQueries customQueries;

    @Override
    public List<ValidatedHipNotifyDTO.MedicalRecordDTO> findMedicalRecordToSendByConsentId(String consentId) throws JsonProcessingException {
        return customQueries.findMedicalRecordToSendByConsentId(consentId);
    }
}

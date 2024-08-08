package org.qwikpe.callback.handler.service.hip.impl;

import org.qwikpe.callback.handler.dto.ConsentRequestHipNotifyDTO;
import org.qwikpe.callback.handler.dto.ValidatedHipNotifyDTO;
import org.qwikpe.callback.handler.sql.CustomQueries;
import org.qwikpe.callback.handler.service.hip.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private CustomQueries customQueries;

    @Override
    public ValidatedHipNotifyDTO findByAbhaAddressAndCareContextAndPatientReferenceAndHiTypes(String abhaAddress, List<ConsentRequestHipNotifyDTO.ConsentDetail.CareContext> careContextList, List<String> hiTypes) throws IOException {
        Set<String> patientReferenceSet = new HashSet<>();
        Set<String> careContextSet = new HashSet<>();
        for (ConsentRequestHipNotifyDTO.ConsentDetail.CareContext careContext : careContextList) {
            patientReferenceSet.add(careContext.getPatientReference());
            careContextSet.add(careContext.getCareContextReference());
        }
        return customQueries.findByAbhaAddressAndCareContextAndPatientReferenceAndHiTypes(abhaAddress, patientReferenceSet, careContextSet, hiTypes);
    }

}

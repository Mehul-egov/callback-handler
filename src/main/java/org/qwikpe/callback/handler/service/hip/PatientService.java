package org.qwikpe.callback.handler.service.hip;

import org.qwikpe.callback.handler.dto.ConsentRequestHipNotifyDTO;
import org.qwikpe.callback.handler.dto.ValidatedHipNotifyDTO;

import java.io.IOException;
import java.util.List;

public interface PatientService {
    ValidatedHipNotifyDTO findByAbhaAddressAndCareContextAndPatientReferenceAndHiTypes(String abhaAddress, List<ConsentRequestHipNotifyDTO.ConsentDetail.CareContext> careContextList, List<String> hiTypes) throws IOException;
}

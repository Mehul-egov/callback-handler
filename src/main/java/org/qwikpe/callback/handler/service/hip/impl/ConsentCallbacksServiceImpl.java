package org.qwikpe.callback.handler.service.hip.impl;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.qwikpe.callback.handler.dto.*;
import org.qwikpe.callback.handler.exception.CallbackResponseNotAsExpectedException;
import org.qwikpe.callback.handler.record.DataPushRecord;
import org.qwikpe.callback.handler.service.EsService;
import org.qwikpe.callback.handler.service.hip.AbdmTrackingService;
import org.qwikpe.callback.handler.service.hip.ConsentCallbacksService;
import org.qwikpe.callback.handler.service.hip.PatientService;
import org.qwikpe.callback.handler.sql.CustomQueries;
import org.qwikpe.callback.handler.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ConsentCallbacksServiceImpl implements ConsentCallbacksService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConsentCallbacksServiceImpl.class);

    @Autowired
    private WebClientUtil webClientUtil;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AbdmTrackingService abdmTrackingService;

    @Autowired
    private EsService esService;

    @Autowired
    private CustomQueries customQueries;

    @Value("${secret.data.transfer.nonce}")
    private String nonce;

    @Value("${secret.data.transfer.publicKey}")
    private String publicKey;

    @Value("${secret.data.transfer.privateKey}")
    private String privateKey;

    private final Map<String, EnumUtil.BundleTypeAndUrl> hiTypeToEnumMapping = new HashMap<>(){{
        put(EnumUtil.HiTypeEnum.OPCONSULTATION.getCode(), EnumUtil.BundleTypeAndUrl.OPCONSULTATION);
        put(EnumUtil.HiTypeEnum.PRESCRIPTION.getCode(), EnumUtil.BundleTypeAndUrl.PRESCRIPTION);
        put(EnumUtil.HiTypeEnum.HEALTHDOCUMENTRECORD.getCode(), EnumUtil.BundleTypeAndUrl.HEALTHDOCUMENTRECORD);
        put(EnumUtil.HiTypeEnum.DIAGNOSTICREPORT.getCode(), EnumUtil.BundleTypeAndUrl.DIAGNOSTICREPORT);
        put(EnumUtil.HiTypeEnum.WELLNESSRECORD.getCode(), EnumUtil.BundleTypeAndUrl.WELLNESSRECORD);
        put(EnumUtil.HiTypeEnum.IMMUNIZATIONRECORD.getCode(), EnumUtil.BundleTypeAndUrl.IMMUNIZATIONRECORD);
        put(EnumUtil.HiTypeEnum.DISCHARGESUMMARY.getCode(), EnumUtil.BundleTypeAndUrl.DISCHARGESUMMARY);
    }};

    @Override
    public void consentRequestOnFetchModes(JsonNode payload) {
        LOGGER.info("consentRequestOnFetchModes :: response: {}", payload);
    }

    @Override
    public void consentRequestInitAuthMode(JsonNode payload) {
        LOGGER.info("consentRequestInitAuthMode :: response: {}", payload);
    }

    @Override
    public void processConsentRequestHipNotify(ConsentRequestHipNotifyDTO consentRequestHipNotifyDTO, String requestId, String xHipId) throws IOException {
        LOGGER.info("processConsentRequestHipNotify :: hip-notified to give linked document with care-context: {}, requestId: {}, xHipId: {}", consentRequestHipNotifyDTO, requestId, xHipId);

        String abhaAddress = consentRequestHipNotifyDTO.getNotification().getConsentDetail().getPatient().getId();
        String consentId = consentRequestHipNotifyDTO.getNotification().getConsentId();
        List<ConsentRequestHipNotifyDTO.ConsentDetail.CareContext> careContextList = consentRequestHipNotifyDTO.getNotification()
                .getConsentDetail().getCareContexts();

        // validating the request
        boolean validationResponse = validateHipNotifyRequest(abhaAddress, consentId, careContextList, consentRequestHipNotifyDTO.getNotification().getConsentDetail().getHiTypes(), requestId, xHipId);

        if (!validationResponse) {
            LOGGER.info("processConsentRequestHipNotify :: Invalid hip-notify request, payload: {}, requestId: {}, " +
                    "xHipId: {}, consentId: {}", consentRequestHipNotifyDTO, requestId, xHipId, consentId);
            throw new CallbackResponseNotAsExpectedException("Invalid hip-notify request");
        }
        Map<String, Object> onNotifyBoyd = new HashMap<>();
        onNotifyBoyd.put("acknowledgement", Map.of("status", "ok", "consentId", consentRequestHipNotifyDTO.getNotification().getConsentId()));
        onNotifyBoyd.put("response", Map.of("requestId", requestId));

        Map<String, String> header = Common.headersWithUtcTimestampAndRequestIdAndCmId();

        webClientUtil.postMethod(URIMapping.ABDM_BASE_URL, URIMapping.ON_NOTIFY, header, onNotifyBoyd, JsonNode.class, Constants.MAX_RETRY, null);

        LOGGER.info("processConsentRequestHipNotify :: Notified the gateway for hip-notify, requestId: {}, xHipId: {}", requestId, xHipId);
    }

    private boolean validateHipNotifyRequest(String abhaAddress, String consentId, List<ConsentRequestHipNotifyDTO.ConsentDetail.CareContext> careContextList, List<String> hiTypes, String requestId, String xHipId) throws IOException {

        ValidatedHipNotifyDTO validatedHipNotifyDTO =
                patientService.findByAbhaAddressAndCareContextAndPatientReferenceAndHiTypes(abhaAddress, careContextList, hiTypes);

        if (validatedHipNotifyDTO == null) {
            return Boolean.FALSE;
        }

        LOGGER.info("validateHipNotifyRequest :: Found the response against the request, founded response: {}, consentId: {}", validatedHipNotifyDTO, consentId);
        AbdmTrackingDTO abdmTrackingDTO = new AbdmTrackingDTO();
        abdmTrackingDTO.setConsentId(consentId);
        abdmTrackingDTO.setAbdmFacilityId(validatedHipNotifyDTO.getQwikpeFacilityId());
        abdmTrackingDTO.setAbdmIdentifier(abhaAddress);
        abdmTrackingDTO.setAbdmIdentifierType("abhaAddress");
        abdmTrackingDTO.setAbdmFacilityId(xHipId);
        abdmTrackingDTO.setQwikpeFacilityId(validatedHipNotifyDTO.getQwikpeFacilityId());
        abdmTrackingDTO.setAbdmRequestId(requestId);
        abdmTrackingDTO.setQwikpeUserType("PATIENT");
        abdmTrackingDTO.setActionType("HIP_NOTIFY_REQUEST_VALIDATION");
        abdmTrackingDTO.setTrackingDetails(Common.JACK_OBJ_MAPPER.writeValueAsString(validatedHipNotifyDTO.getMedicalRecordDTOList()));

        webClientUtil.postMethod(URIMapping.REGISTRATION_APPLICATION_BASE_URL, URIMapping.ABDM_TRACKING_DETAILS,
                Collections.emptyMap(), abdmTrackingDTO, String.class, Constants.MAX_RETRY, new LinkedMultiValueMap<>(){{
                    add("newTrack", "true");
                }});

        return Boolean.TRUE;
    }

    @Override
    public void hipHealthInformationRequest(HipRequestDTO hipRequestDTO, String xHipId, String requestId) throws IOException {
        LOGGER.info("healthInformationRequest :: get the request to encrypt the data and send it to the callback url, payload: {}, requestId: {}, xHipId: {}", hipRequestDTO, requestId, xHipId);

        List<String> medicalRecordIdList = new ArrayList<>();
        Map<Long, String> medicalRecordIdTypeMap = new HashMap<>();
        Set<Long> medicalRecordCommonDetailIdSet = new HashSet<>();
        Map<Long, Long> mrAndMrcdMap = new HashMap<>();
        Map<Long, MedicalRecordCommonDetailsDTO> mrcdIdAndMrcdDTOMap = new HashMap<>();
        Map<Long, String> mrAndCCMap = new HashMap<>();
        String consentId = hipRequestDTO.getHiRequest().getConsent().getId();
        List<ValidatedHipNotifyDTO.MedicalRecordDTO> medicalRecordDTOList = abdmTrackingService.findMedicalRecordToSendByConsentId(consentId);

        for (ValidatedHipNotifyDTO.MedicalRecordDTO medicalRecordDTO : medicalRecordDTOList) {
            medicalRecordIdList.add(String.valueOf(medicalRecordDTO.getMedicalRecordId()));
            medicalRecordIdTypeMap.put(medicalRecordDTO.getMedicalRecordId(), medicalRecordDTO.getMedicalRecordType());
            medicalRecordCommonDetailIdSet.add(medicalRecordDTO.getMedicalRecordCommonDetailId());
            mrAndMrcdMap.put(medicalRecordDTO.getMedicalRecordId(), medicalRecordDTO.getMedicalRecordCommonDetailId());
            mrAndCCMap.put(medicalRecordDTO.getMedicalRecordId(), medicalRecordDTO.getCareContextReferenceNumber());
        }

        List<MedicalRecordCommonDetailsDTO> medicalRecordCommonDetails = customQueries
                .findAllByMedicalRecordCommonDetailsId(medicalRecordCommonDetailIdSet);

        mrcdIdAndMrcdDTOMap = medicalRecordCommonDetails.stream().collect(Collectors.toMap(MedicalRecordCommonDetailsDTO::getId, mrcd -> mrcd));

        Map<Long, Map<String, Object>> commonDetailIdMap = new HashMap<>();
        prepareCommonDetail(medicalRecordCommonDetails, commonDetailIdMap);

        SearchResponse<Map> medicalRecords =
                esService.findByIds(EnumUtil.IndexEnum.MEDICAL_RECORDS.getName(), medicalRecordIdList, Map.class);

        if (medicalRecords.hits().hits().isEmpty()) {
            LOGGER.info("healthInformationRequest :: No medical records found for consentId: {}, with medicalRecordIdLIst: {}", consentId, medicalRecords);
            throw new RuntimeException("No medical records found for consentId: " + consentId);
        }

        int pageNumber = -1;
        int pageCount = 1;

        for (Hit<Map> hit : medicalRecords.hits().hits()) {
            pageNumber++;

            Long medicalRecordId = Long.valueOf(hit.id());
            String medicalRecordType = medicalRecordIdTypeMap.get(medicalRecordId);
            String visitDate = mrcdIdAndMrcdDTOMap.get(mrAndMrcdMap.get(medicalRecordId)).getVisitDate();

            Map<String, Object> commonDetails = commonDetailIdMap.get(mrAndMrcdMap.get(medicalRecordId));

            Map<String, Object> fhirTransformationPayload = new HashMap<>(commonDetails);
            fhirTransformationPayload.put("bundleType", hiTypeToEnumMapping.get(medicalRecordType).getBundleType());
            fhirTransformationPayload.putAll(hit.source());
            if (medicalRecordType.equals(EnumUtil.HiTypeEnum.OPCONSULTATION.getCode())) {
                fhirTransformationPayload.put("visitDate", visitDate);
            } else {
                fhirTransformationPayload.put("authoredOn", visitDate);
            }

            LOGGER.info("hipHealthInformationRequest :: fhirPayload: {}", fhirTransformationPayload);
            Map<String, Object> response = webClientUtil.postMethod("http://localhost:8085",
                    hiTypeToEnumMapping.get(medicalRecordType).getUrl(),
                    Collections.emptyMap(), fhirTransformationPayload,
                    Map.class, Constants.MAX_RETRY, null);

            String encryptedData = encryptData(response, hipRequestDTO.getHiRequest().getKeyMaterial().getNonce(),
                    hipRequestDTO.getHiRequest().getKeyMaterial().getDhPublicKey().getKeyValue());

            DataPushRecord dataPushRecord = new DataPushRecord(pageNumber, pageCount,
                    hipRequestDTO.getTransactionId(), encryptedData, mrAndCCMap.get(medicalRecordId),
                    hipRequestDTO.getHiRequest().getDataPushUrl(),
                    hipRequestDTO.getHiRequest().getKeyMaterial().getDhPublicKey().getExpiry());
            callDataPushUrl(dataPushRecord);
        }
    }

    private void callDataPushUrl(DataPushRecord dataPushRecord) {

        DataTransferDTO.Entry entry = DataTransferDTO.Entry.builder()
                .content(dataPushRecord.content())
                .media("application/fhir+json")
                .checksum("")
                .careContextReference(dataPushRecord.careContextReference())
                .build();

        DataTransferDTO.DhPublicKey dhPublicKey = DataTransferDTO.DhPublicKey.builder()
                .expiry(dataPushRecord.expiry())
                .parameters("Curve25519/32byte random key")
                .keyValue(publicKey)
                .build();

        DataTransferDTO.KeyMaterial keyMaterial = DataTransferDTO.KeyMaterial.builder()
                .cryptoAlg("ECDH")
                .curve("Curve25519")
                .nonce(nonce)
                .dhPublicKey(dhPublicKey)
                .build();

        DataTransferDTO dataTransferDTO = DataTransferDTO.builder()
                .transactionId(dataPushRecord.transactionId())
                .pageNumber(dataPushRecord.pageNumber())
                .pageCount(dataPushRecord.pageCount())
                .keyMaterial(keyMaterial)
                .entries(List.of(entry))
                .build();

        webClientUtil.postMethod(dataPushRecord.dataPushUrl(), Collections.emptyMap(),
                dataTransferDTO, String.class, Constants.MAX_RETRY);

    }

    private void prepareCommonDetail(List<MedicalRecordCommonDetailsDTO> medicalRecordCommonDetails,
                                     Map<Long, Map<String, Object>> commonDetailIdMap) {

        for (MedicalRecordCommonDetailsDTO medicalRecordCommonDetail : medicalRecordCommonDetails) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("careContextReference", medicalRecordCommonDetail.getCareContextReference());
            temp.put("encounter", medicalRecordCommonDetail.getEncounter());

            Map<String, Object> patient = new HashMap<>();
            patient.put("name", medicalRecordCommonDetail.getPatientName());
            patient.put("patientReference", medicalRecordCommonDetail.getPatientReference());
            patient.put("gender", medicalRecordCommonDetail.getPatientGender());
            patient.put("birthDate", medicalRecordCommonDetail.getPatientBirthDate());

            temp.put("patient", patient);

            Map<String, Object> practitioner = new HashMap<>();
            practitioner.put("name", medicalRecordCommonDetail.getPractitionerName());
            practitioner.put("practitionerId", medicalRecordCommonDetail.getPractitionerId());

            temp.put("practitioners", List.of(practitioner));

            Map<String, Object> organization = new HashMap<>();
            organization.put("facilityName", medicalRecordCommonDetail.getFacilityName());
            organization.put("facilityId", medicalRecordCommonDetail.getFacilityId());

            temp.put("organisation", organization);

            commonDetailIdMap.put(medicalRecordCommonDetail.getId(), temp);
        }
    }

    private String encryptData(Map<String, Object> data, String requesterNonce,
                             String requesterPublicKey) throws JsonProcessingException {
        String str = "\"{\"resourceType\":\"Bundle\",\"id\":\"3708574b-7b27-40c7-a295-b5342971b04b\",\"meta\":{\"versionId\":\"1\",\"lastUpdated\":\"2024-08-07T10:59:58.790+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/DocumentBundle\"],\"security\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-Confidentiality\",\"code\":\"V\",\"display\":\"veryrestricted\"}]},\"identifier\":{\"system\":\"https://ABDM_WRAPPER/bundle\",\"value\":\"Livetestingofcare-cotext1\"},\"type\":\"document\",\"timestamp\":\"2024-08-07T10:59:58.790+05:30\",\"entry\":[{\"fullUrl\":\"Composition/3f70e89c-b9df-4c43-9fa4-45ea6c417b3a\",\"resource\":{\"resourceType\":\"Composition\",\"id\":\"3f70e89c-b9df-4c43-9fa4-45ea6c417b3a\",\"identifier\":{\"system\":\"https://ABDM_WRAPPER/bundle\",\"value\":\"b205ef1e-f4e5-4b95-a67c-0b86e761dc3e\"},\"status\":\"final\",\"type\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"371530004\",\"display\":\"Clinicalconsultationreport\"}]},\"subject\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"display\":\"MeetJoshi\"},\"encounter\":{\"reference\":\"Encounter/6b8b8085-8787-4152-af65-608467c7eaab\"},\"date\":\"2024-08-06T17:46:38+05:30\",\"author\":[{\"reference\":\"Practitioner/HP_87968b2b-5eaa-4073-bf87-2f2732912dda\",\"display\":\"Dr.HitenKhatri\"}],\"title\":\"ConsultationReport\",\"custodian\":{\"reference\":\"Organisation/c1e1cb10-3dcd-4877-b9c7-fe6ccb264993\",\"display\":\"Predator_HIP\"},\"section\":[{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"422843007\",\"display\":\"Chiefcomplaintsection\"}],\"text\":\"ChiefComplaints\"},\"entry\":[{\"reference\":\"ChiefComplaints/aa1d3e90-e628-42bb-aed2-0a8d50530512\"}]},{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"425044008\",\"display\":\"Physicalexamsection\"}],\"text\":\"PhysicalExamination\"},\"entry\":[{\"reference\":\"PhysicalExamination/c6696110-507e-4433-a5c6-f8731fef3b82\"}]},{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"722446000\",\"display\":\"Allergyrecord\"}],\"text\":\"AllergySection\"},\"entry\":[{\"reference\":\"AllergyIntolerance/e22134ab-787a-4d1f-a407-00e0647e3419\"}]},{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"371529009\",\"display\":\"Historyandphysicalreport\"}],\"text\":\"MedicalHistory\"},\"entry\":[{\"reference\":\"MedicalHistory/e6248bd6-6ba6-48f0-adb7-6c75957cbee2\"}]},{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"422432008\",\"display\":\"Familyhistorysection\"}],\"text\":\"FamilyHistory\"},\"entry\":[{\"reference\":\"FamilyHistory/174ce205-8903-40ea-a4e0-41bb02386e5f\"}]},{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"721963009\",\"display\":\"Orderdocument\"}],\"text\":\"InvestigationAdvice\"},\"entry\":[{\"reference\":\"InvestigationAdvice/2e15cac9-478c-4074-9860-6dccb32e0650\"}]},{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"721912009\",\"display\":\"Medicationsummarydocument\"}],\"text\":\"Medicationsummarydocument\"},\"entry\":[{\"reference\":\"FamilyHistory/b49d9213-9dea-4711-9b0d-3438ddc77caa\"},{\"reference\":\"FamilyHistory/34a06e6d-10f7-4fe4-a205-6151e5fe6461\"}]},{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"390906007\",\"display\":\"Follow-upencounter\"}],\"text\":\"FollowUp\"},\"entry\":[{\"reference\":\"FollowUp/50f4bdff-c3ac-4090-9896-6f15268cb5a6\"}]},{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"371525003\",\"display\":\"Clinicalprocedurereport\"}],\"text\":\"Procedure\"},\"entry\":[{\"reference\":\"Procedure/023f1add-475c-4659-861e-114de6f34012\"}]},{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"306206005\",\"display\":\"Referraltoservice\"}],\"text\":\"Referral\"},\"entry\":[{\"reference\":\"Referral/99279d7a-b436-43bc-aacf-fe6f226719de\"}]},{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"404684003\",\"display\":\"Clinicalfinding\"}],\"text\":\"OtherObservations\"},\"entry\":[{\"reference\":\"OtherObservations/588567be-a5dd-4a16-9bf1-675ad41e637a\"}]},{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"371530004\",\"display\":\"Clinicalconsultationreport\"}],\"text\":\"DocumentReference\"},\"entry\":[{\"reference\":\"DocumentReference/489d8967-bd00-4887-8231-9700c48982ad\"}]}]}},{\"fullUrl\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"resource\":{\"resourceType\":\"Patient\",\"id\":\"ef49d964-cb42-41d6-a052-c68e8f690583\",\"meta\":{\"versionId\":\"1\",\"lastUpdated\":\"2024-08-07T10:59:58.538+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/Patient\"]},\"identifier\":[{\"type\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v2-0203\",\"code\":\"MR\",\"display\":\"Medicalrecordnumber\"}]},\"system\":\"https://healthid.abdm.gov.in\",\"value\":\"patient-referencenewtesting\"}],\"name\":[{\"text\":\"MeetJoshi\"}],\"gender\":\"male\",\"birthDate\":\"2001-04-27\"}},{\"fullUrl\":\"Practitioner/HP_87968b2b-5eaa-4073-bf87-2f2732912dda\",\"resource\":{\"resourceType\":\"Practitioner\",\"id\":\"HP_87968b2b-5eaa-4073-bf87-2f2732912dda\",\"meta\":{\"versionId\":\"1\",\"lastUpdated\":\"2024-08-07T10:59:58.549+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/Practitioner\"]},\"identifier\":[{\"type\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v2-0203\",\"code\":\"MR\",\"display\":\"Medicalrecordnumber\"}]},\"system\":\"https://doctor.abdm.gov.in\",\"value\":\"HP_87968b2b-5eaa-4073-bf87-2f2732912dda\"}],\"name\":[{\"text\":\"Dr.HitenKhatri\"}]}},{\"fullUrl\":\"Encounter/6b8b8085-8787-4152-af65-608467c7eaab\",\"resource\":{\"resourceType\":\"Encounter\",\"id\":\"6b8b8085-8787-4152-af65-608467c7eaab\",\"meta\":{\"lastUpdated\":\"2024-08-07T10:59:58.560+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/Encounter\"]},\"status\":\"in-progress\",\"class\":{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-Confidentiality\",\"code\":\"AMB\",\"display\":\"Ambulatory\"},\"subject\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"display\":\"MeetJoshi\"},\"period\":{\"start\":\"2024-08-06T17:46:38+05:30\"}}},{\"fullUrl\":\"Organisation/c1e1cb10-3dcd-4877-b9c7-fe6ccb264993\",\"resource\":{\"resourceType\":\"Organization\",\"id\":\"c1e1cb10-3dcd-4877-b9c7-fe6ccb264993\",\"meta\":{\"versionId\":\"1\",\"lastUpdated\":\"2024-08-07T10:59:58.500+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/Organization\"]},\"identifier\":[{\"type\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v2-0203\",\"code\":\"PRN\",\"display\":\"Providernumber\"}]},\"system\":\"https://facility.abdm.gov.in\",\"value\":\"Predator_HIP\"}],\"name\":\"Predator_HIP\"}},{\"fullUrl\":\"ChiefComplaints/aa1d3e90-e628-42bb-aed2-0a8d50530512\",\"resource\":{\"resourceType\":\"Condition\",\"id\":\"aa1d3e90-e628-42bb-aed2-0a8d50530512\",\"meta\":{\"lastUpdated\":\"2024-08-07T10:59:58.692+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/Condition\"]},\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Sugar\"}],\"text\":\"Sugar\"},\"subject\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"display\":\"MeetJoshi\"},\"onsetPeriod\":{\"start\":\"2018-04-27T00:00:00+05:30\",\"end\":\"2018-05-26T00:00:00+05:30\"},\"recordedDate\":\"2024-05-20T00:00:00+05:30\"}},{\"fullUrl\":\"PhysicalExamination/c6696110-507e-4433-a5c6-f8731fef3b82\",\"resource\":{\"resourceType\":\"Observation\",\"id\":\"c6696110-507e-4433-a5c6-f8731fef3b82\",\"meta\":{\"lastUpdated\":\"2024-08-07T10:59:58.706+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/Observation\"]},\"status\":\"final\",\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Height\"}],\"text\":\"Height\"},\"subject\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"display\":\"MeetJoshi\"},\"performer\":[{\"reference\":\"Practitioner/HP_87968b2b-5eaa-4073-bf87-2f2732912dda\",\"display\":\"Dr.HitenKhatri\"}],\"valueCodeableConcept\":{\"text\":\"Normal\"}}},{\"fullUrl\":\"AllergyIntolerance/e22134ab-787a-4d1f-a407-00e0647e3419\",\"resource\":{\"resourceType\":\"AllergyIntolerance\",\"id\":\"e22134ab-787a-4d1f-a407-00e0647e3419\",\"meta\":{\"lastUpdated\":\"2024-08-07T10:59:58.708+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/AllergyIntolerance\"]},\"clinicalStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical\",\"code\":\"Active\",\"display\":\"Active\"}]},\"type\":\"allergy\",\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"609328004\",\"display\":\"Allergy\"}],\"text\":\"Walnuts\"},\"patient\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"display\":\"MeetJoshi\"},\"recordedDate\":\"2024-08-06T17:46:38+05:30\",\"recorder\":{\"reference\":\"Practitioner/HP_87968b2b-5eaa-4073-bf87-2f2732912dda\",\"display\":\"MeetJoshi\"}}},{\"fullUrl\":\"MedicalHistory/e6248bd6-6ba6-48f0-adb7-6c75957cbee2\",\"resource\":{\"resourceType\":\"Condition\",\"id\":\"e6248bd6-6ba6-48f0-adb7-6c75957cbee2\",\"meta\":{\"lastUpdated\":\"2024-08-07T10:59:58.713+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/Condition\"]},\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Sugar\"}],\"text\":\"Sugar\"},\"subject\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"display\":\"MeetJoshi\"},\"onsetPeriod\":{\"start\":\"2018-04-27T00:00:00+05:30\",\"end\":\"2018-05-26T00:00:00+05:30\"},\"recordedDate\":\"2024-05-20T00:00:00+05:30\"}},{\"fullUrl\":\"FamilyHistory/174ce205-8903-40ea-a4e0-41bb02386e5f\",\"resource\":{\"resourceType\":\"FamilyMemberHistory\",\"id\":\"174ce205-8903-40ea-a4e0-41bb02386e5f\",\"meta\":{\"lastUpdated\":\"2024-08-07T10:59:58.718+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/FamilyMemberHistory\"]},\"status\":\"completed\",\"patient\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"display\":\"MeetJoshi\"},\"relationship\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Friend\"}],\"text\":\"Friend\"},\"condition\":[{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Toxic\"}],\"text\":\"Toxic\"}}]}},{\"fullUrl\":\"InvestigationAdvice/2e15cac9-478c-4074-9860-6dccb32e0650\",\"resource\":{\"resourceType\":\"ServiceRequest\",\"id\":\"2e15cac9-478c-4074-9860-6dccb32e0650\",\"meta\":{\"lastUpdated\":\"2024-08-07T10:59:58.722+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/ServiceRequest\"]},\"status\":\"active\",\"intent\":\"proposal\",\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"X-RAY\"}],\"text\":\"X-RAY\"},\"subject\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"display\":\"MeetJoshi\"},\"authoredOn\":\"2024-08-06T17:46:38+05:30\",\"requester\":{\"reference\":\"Practitioner/HP_87968b2b-5eaa-4073-bf87-2f2732912dda\",\"display\":\"Dr.HitenKhatri\"},\"performer\":[{\"reference\":\"Practitioner/HP_87968b2b-5eaa-4073-bf87-2f2732912dda\",\"display\":\"Dr.HitenKhatri\"}],\"specimen\":[{\"display\":\"Jhonsons\"}]}},{\"fullUrl\":\"MedicationRequest/b49d9213-9dea-4711-9b0d-3438ddc77caa\",\"resource\":{\"resourceType\":\"MedicationRequest\",\"id\":\"b49d9213-9dea-4711-9b0d-3438ddc77caa\",\"meta\":{\"lastUpdated\":\"2024-08-07T10:59:58.729+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/MedicationRequest\"]},\"status\":\"completed\",\"intent\":\"order\",\"medicationCodeableConcept\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Aspirin75mgoraltablet\"}],\"text\":\"Aspirin75mgoraltablet\"},\"subject\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"display\":\"MeetJoshi\"},\"authoredOn\":\"2024-08-06T17:46:38+05:30\",\"requester\":{\"reference\":\"Practitioner/HP_87968b2b-5eaa-4073-bf87-2f2732912dda\",\"display\":\"Dr.HitenKhatri\"},\"dosageInstruction\":[{\"text\":\"1-0-1\",\"additionalInstruction\":[{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Takethemafterfood\"}],\"text\":\"Takethemafterfood\"}],\"timing\":{\"repeat\":{\"frequency\":2,\"period\":5,\"periodUnit\":\"d\"}},\"route\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Oral\"}],\"text\":\"Oral\"},\"method\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"swallow\"}],\"text\":\"swallow\"}}]}},{\"fullUrl\":\"MedicationRequest/34a06e6d-10f7-4fe4-a205-6151e5fe6461\",\"resource\":{\"resourceType\":\"MedicationRequest\",\"id\":\"34a06e6d-10f7-4fe4-a205-6151e5fe6461\",\"meta\":{\"lastUpdated\":\"2024-08-07T10:59:58.744+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/MedicationRequest\"]},\"status\":\"completed\",\"intent\":\"order\",\"medicationCodeableConcept\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Disprin\"}],\"text\":\"Disprin\"},\"subject\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"display\":\"MeetJoshi\"},\"authoredOn\":\"2024-08-06T17:46:38+05:30\",\"requester\":{\"reference\":\"Practitioner/HP_87968b2b-5eaa-4073-bf87-2f2732912dda\",\"display\":\"Dr.HitenKhatri\"},\"dosageInstruction\":[{\"text\":\"0-0-1\",\"additionalInstruction\":[{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Takethembeforefood\"}],\"text\":\"Takethembeforefood\"}],\"timing\":{\"repeat\":{\"frequency\":1,\"period\":2,\"periodUnit\":\"d\"}},\"route\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Syrup\"}],\"text\":\"Syrup\"},\"method\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"drink\"}],\"text\":\"drink\"}}]}},{\"fullUrl\":\"FollowUp/50f4bdff-c3ac-4090-9896-6f15268cb5a6\",\"resource\":{\"resourceType\":\"Appointment\",\"id\":\"50f4bdff-c3ac-4090-9896-6f15268cb5a6\",\"status\":\"proposed\",\"serviceType\":[{\"text\":\"OPConsultation\"}],\"reasonCode\":[{\"text\":\"General\"}],\"start\":\"2024-05-20T00:00:00.000+05:30\",\"participant\":[{\"actor\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\"},\"status\":\"accepted\"}]}},{\"fullUrl\":\"Procedure/023f1add-475c-4659-861e-114de6f34012\",\"resource\":{\"resourceType\":\"Procedure\",\"id\":\"023f1add-475c-4659-861e-114de6f34012\",\"meta\":{\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/Procedure\"]},\"status\":\"in-progress\",\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Operation\"}],\"text\":\"Operation\"},\"subject\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\"},\"performedDateTime\":\"2001-04-20\",\"reasonCode\":[{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Severe\"}],\"text\":\"Severe\"}],\"outcome\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Healthy\"}],\"text\":\"Healthy\"}}},{\"fullUrl\":\"Referral/99279d7a-b436-43bc-aacf-fe6f226719de\",\"resource\":{\"resourceType\":\"ServiceRequest\",\"id\":\"99279d7a-b436-43bc-aacf-fe6f226719de\",\"meta\":{\"lastUpdated\":\"2024-08-07T10:59:58.765+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/ServiceRequest\"]},\"status\":\"active\",\"intent\":\"proposal\",\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"Y-RAY\"}],\"text\":\"Y-RAY\"},\"subject\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"display\":\"MeetJoshi\"},\"authoredOn\":\"2024-08-06T17:46:38+05:30\",\"requester\":{\"reference\":\"Practitioner/HP_87968b2b-5eaa-4073-bf87-2f2732912dda\",\"display\":\"Dr.HitenKhatri\"},\"performer\":[{\"reference\":\"Practitioner/HP_87968b2b-5eaa-4073-bf87-2f2732912dda\",\"display\":\"Dr.HitenKhatri\"}],\"specimen\":[{\"display\":\"Rock\"}]}},{\"fullUrl\":\"OtherObservations/588567be-a5dd-4a16-9bf1-675ad41e637a\",\"resource\":{\"resourceType\":\"Observation\",\"id\":\"588567be-a5dd-4a16-9bf1-675ad41e637a\",\"meta\":{\"lastUpdated\":\"2024-08-07T10:59:58.768+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/Observation\"]},\"status\":\"final\",\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"261665006\",\"display\":\"weight\"}],\"text\":\"weight\"},\"subject\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"display\":\"MeetJoshi\"},\"performer\":[{\"reference\":\"Practitioner/HP_87968b2b-5eaa-4073-bf87-2f2732912dda\",\"display\":\"Dr.HitenKhatri\"}],\"valueQuantity\":{\"value\":90,\"unit\":\"KG\"}}},{\"fullUrl\":\"DocumentReference/489d8967-bd00-4887-8231-9700c48982ad\",\"resource\":{\"resourceType\":\"DocumentReference\",\"id\":\"489d8967-bd00-4887-8231-9700c48982ad\",\"meta\":{\"lastUpdated\":\"2024-08-07T10:59:58.774+05:30\",\"profile\":[\"https://nrces.in/ndhm/fhir/r4/StructureDefinition/DocumentReference\"]},\"identifier\":[{\"type\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"371530004\",\"display\":\"Clinicalconsultationreport\"}],\"text\":\"OPrecord\"},\"system\":\"https://facility.abdm.gov.in\",\"value\":\"c1e1cb10-3dcd-4877-b9c7-fe6ccb264993\"}],\"status\":\"current\",\"docStatus\":\"final\",\"subject\":{\"reference\":\"Patient/ef49d964-cb42-41d6-a052-c68e8f690583\",\"display\":\"MeetJoshi\"},\"content\":[{\"attachment\":{\"contentType\":\"application/pdf\",\"title\":\"OPrecord\",\"creation\":\"2024-08-07T10:59:58+05:30\"}}]}}]}\"";

        FhirDataEncryptionDTO fhirDataEncryptionDTO = FhirDataEncryptionDTO.builder()
                .stringToEncrypt(str)
                .senderNonce(nonce)
                .requesterNonce(requesterNonce)
                .senderPrivateKey(privateKey)
                .requesterPublicKey(requesterPublicKey)
                .build();

        return webClientUtil
                .postMethod("http://localhost:9093",
                        URIMapping.ENCRYPT, Collections.emptyMap(),
                        fhirDataEncryptionDTO, String.class, Constants.MAX_RETRY, null);

    }

    @Override
    public void consentRequestOnConfirm(JsonNode payload) {
        LOGGER.info("consentRequestOnConfirm :: response: {}", payload);

        Map<String, Object> registerPatientPayload = new HashMap<>();

        String healthId = payload.get("auth").get("patient").get("id").asText();
        String linkToken = payload.get("auth").get("accessToken").asText();
        registerPatientPayload.put("healthId", healthId);
        registerPatientPayload.put("linkToken", linkToken);

        webClientUtil.postMethod(URIMapping.REGISTRATION_APPLICATION_BASE_URL, URIMapping.REGISTER_PATIENT,
                Collections.emptyMap(), registerPatientPayload, JsonNode.class, Constants.MAX_RETRY, null);

    }

    @Override
    public void onGenerateToken(String xHipId, Map<String, Object> requestBody, String abdmRequestId) {
        try {
            LOGGER.info("onGenerateToken :: response: {}, xHipId: {}", requestBody, xHipId);

            if (requestBody.get("abhaAddress") == null || requestBody.get("linkToken") == null ) {
                LOGGER.error("onGenerateToken :: payload is not as expected");
                throw new CallbackResponseNotAsExpectedException("Callback response not as expected");
            }

            String healthId = requestBody.get("abhaAddress").toString();
            String linkToken = requestBody.get("linkToken").toString();

            Map<String, Object> registerPatientPayload =
                    Map.of("healthId", healthId, "linkToken", linkToken, "hipId", xHipId);

            webClientUtil.postMethod(URIMapping.REGISTRATION_APPLICATION_BASE_URL, URIMapping.REGISTER_PATIENT,
                    Collections.emptyMap(), registerPatientPayload, JsonNode.class, Constants.MAX_RETRY, null);

            LOGGER.info("onGenerateToken :: patient-registered successfully");
        } catch (CallbackResponseNotAsExpectedException callbackResponseNotAsExpectedException) {
            Map<String, Object> requestIdMap = Common.JACK_OBJ_MAPPER.
                    convertValue(requestBody.remove("response"), new TypeReference<>() {
                    });
            Map<String, Object> errorMap = Common.JACK_OBJ_MAPPER.convertValue(
                    requestBody.remove("error"), new TypeReference<>() {
                    }
            );

            AbdmTrackingDTO abdmTrackingDTO = new AbdmTrackingDTO();
            abdmTrackingDTO.setAbdmRequestId(abdmRequestId);
            abdmTrackingDTO.setAbdmFacilityId(xHipId);
            abdmTrackingDTO.setAbdmFacilityType("HIP");
            abdmTrackingDTO.setTrackingDetails(errorMap.get("message").toString());
            abdmTrackingDTO.setActionType("ON_GENERATE_TOKEN_RESPONSE");
            abdmTrackingDTO.setQwikpeRequestId(requestIdMap.get("requestId").toString());
            abdmTrackingDTO.setAbdmIdentifierType("abhaAddress");
            abdmTrackingDTO.setQwikpeUserType("PATIENT");
            webClientUtil.postMethod(URIMapping.REGISTRATION_APPLICATION_BASE_URL, URIMapping.ABDM_TRACKING_DETAILS,
                    Collections.emptyMap(), abdmTrackingDTO, String.class, Constants.MAX_RETRY, null);
        }
    }


}

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

        SearchResponse<Map> response = esService.findByFields(List.of(EnumUtil.IndexEnum.FACILITY.getName()),
                Map.of("abdmFacilityId", xHipId), Map.class, List.of("abdmFacilityId, hprDetailsId"));

        String consentId = consentRequestHipNotifyDTO.getNotification().getConsentId();
        if (response.hits().hits().isEmpty()) {
            LOGGER.error("processConsentRequestHipNotify :: Patient is not registered with requested hip, payload: {}, requestId: {}," +
                    "xHipId: {}, consentId: {}", consentRequestHipNotifyDTO, requestId, xHipId, consentId);

            throw new CallbackResponseNotAsExpectedException("Patient is not registered with requested hip");
        } else if (response.hits().hits().size() > 1) {
            LOGGER.error("processConsentRequestHipNotify :: Multiple hip record found given hipId: {}", xHipId);
            throw new RuntimeException("Internal Server Error");
        }
        Hit<Map> hit = response.hits().hits().get(0);

        String qwikpeFacilityId = hit.id();
        String abhaAddress = consentRequestHipNotifyDTO.getNotification().getConsentDetail().getPatient().getId();

        List<ConsentRequestHipNotifyDTO.ConsentDetail.CareContext> careContextList = consentRequestHipNotifyDTO.getNotification()
                .getConsentDetail().getCareContexts();

        // validating the request
        boolean validationResponse = validateHipNotifyRequest(abhaAddress, consentId, careContextList,
                consentRequestHipNotifyDTO.getNotification().getConsentDetail().getHiTypes(), requestId, xHipId, qwikpeFacilityId);

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

    private boolean validateHipNotifyRequest(String abhaAddress, String consentId,
                                             List<ConsentRequestHipNotifyDTO.ConsentDetail.CareContext> careContextList,
                                             List<String> hiTypes, String requestId, String xHipId, String qwikpeFacilityId) throws IOException {

        ValidatedHipNotifyDTO validatedHipNotifyDTO =
                patientService.findByAbhaAddressAndCareContextAndPatientReferenceAndHiTypesAndQwikpeFacilityId(abhaAddress, careContextList, hiTypes, qwikpeFacilityId);

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
            Map<String, Object> response = webClientUtil.postMethod(URIMapping.FHIR_BASE_URL,
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

        String str = Common.JACK_OBJ_MAPPER.writeValueAsString(data);

        FhirDataEncryptionDTO fhirDataEncryptionDTO = FhirDataEncryptionDTO.builder()
                .stringToEncrypt(str)
                .senderNonce(nonce)
                .requesterNonce(requesterNonce)
                .senderPrivateKey(privateKey)
                .requesterPublicKey(requesterPublicKey)
                .build();

        return webClientUtil
                .postMethod(URIMapping.QWIKPE_UTILITY_BASE_URL,
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

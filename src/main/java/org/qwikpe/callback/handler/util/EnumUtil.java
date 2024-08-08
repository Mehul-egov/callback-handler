package org.qwikpe.callback.handler.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class EnumUtil {

    @AllArgsConstructor
    @Getter
    public enum IndexEnum {
        FACILITY("facility"), MEDICAL_RECORDS("medical_records");

        final String name;
    }

    @AllArgsConstructor
    @Getter
    public enum HiTypeEnum {

        PRESCRIPTION("Prescription", "Prescription", "440545006"),
        DIAGNOSTICREPORT("DiagnosticReport", "Diagnostic Report", "721981007"),
        OPCONSULTATION("OPConsultation", "OP Consultation", "371530004"),
        DISCHARGESUMMARY("DischargeSummary", "Discharge Summary", "373942005"),
        IMMUNIZATIONRECORD("ImmunizationRecord", "Immunization Record", "41000179103"),
        HEALTHDOCUMENTRECORD("HealthDocumentRecord", "Record artifact", "419891008"),
        WELLNESSRECORD("WellnessRecord", "Wellness Record", "N/A (Should match exact text Wellness record)");
        final String code;

        final String display;

        final String snomedCtCode;
    }

    @AllArgsConstructor
    @Getter
    public enum BundleTypeAndUrl {

        PRESCRIPTION("PrescriptionRecord", "/v1/bundle/prescription"),
        DIAGNOSTICREPORT("DiagnosticReportRecord", "/v1/bundle/diagnostic-report"),
        OPCONSULTATION("OPConsultRecord", "/v1/bundle/op-consultation"),
        DISCHARGESUMMARY("DischargeSummaryRecord", "/v1/bundle/discharge-summary"),
        IMMUNIZATIONRECORD("ImmunizationRecord", "/v1/bundle/immunization"),
        HEALTHDOCUMENTRECORD("HealthDocumentRecord", "/v1/bundle/health-document"),
        WELLNESSRECORD("WellnessRecord", "/v1/bundle/wellness-record");

        final String bundleType;

        final String url;
    }
}

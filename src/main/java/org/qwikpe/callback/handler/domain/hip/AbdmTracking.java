package org.qwikpe.callback.handler.domain.hip;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

public class AbdmTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String consentId;

    private String qwikpeRequestId;

    private String abdmRequestId;

    private String transactionId;

    @Column(nullable = false)
    private String actionType;

    private String qwikpeUserId;

    @Column(nullable = false)
    private String qwikpeUserType;

    @Column(nullable = false)
    private String abdmIdentifier;

    private String qwikpeFacilityId;

    @Column(nullable = false)
    private String abdmIdentifierType;

    private String abdmFacilityId;

    private String abdmFacilityType;

    @Column(nullable = false)
    private String trackingDetails;

    @CreationTimestamp
    private Timestamp createdTime;
}

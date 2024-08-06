package org.qwikpe.callback.handler.domain.b2b.uhi;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "credentials_info", schema = "master")
@Entity
public class CredentialsInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String domain;

    @Column(name = "domain_name")
    private String domainName;

    private String credential;

    @Column(name = "subscriber_id")
    private String subscriberId;

    @Column(name = "public_key_id")
    private String publicKeyId;

    @Column(name = "public_key")
    private String publicKey;

    @Column(name = "private_key")
    private String privateKey;

    private String region;

    @Column(name = "valid_until")
    private String validUntil;
}

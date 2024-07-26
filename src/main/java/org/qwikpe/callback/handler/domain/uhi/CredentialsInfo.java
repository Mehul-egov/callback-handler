package org.qwikpe.callback.handler.domain.uhi;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @GeneratedValue
    private Long id;

    private String domain;

    private String domainName;

    private String credential;

    private String subscriberId;

    private String publicKeyId;

    private String publicKey;

    private String privateKey;

    private String region;

    private String validUntil;
}

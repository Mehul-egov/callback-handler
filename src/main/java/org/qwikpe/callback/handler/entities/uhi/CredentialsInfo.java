package org.qwikpe.callback.handler.entities.uhi;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "master")
@Entity
public class CredentialsInfo {

    @Id
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

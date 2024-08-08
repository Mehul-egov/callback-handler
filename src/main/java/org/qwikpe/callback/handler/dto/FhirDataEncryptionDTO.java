package org.qwikpe.callback.handler.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FhirDataEncryptionDTO {

    private String stringToEncrypt;

    private String senderNonce;

    private String requesterNonce;

    private String senderPrivateKey;

    private String requesterPublicKey;;
}

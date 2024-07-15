package org.qwikpe.callback.handler.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class HeaderGenerator {

    private static final String SIGNATURE_ALGO = EdDSAParameterSpec.Ed25519;

    private String provider = "BC";

    public HeaderGenerator(String provider) {
        super();
        this.provider = provider;
    }

    public HeaderGenerator(){}

    public String getHeader(String subsId,String pub_key_id,String privateKey,String payload) throws Exception{

        return this.generateAuthorizationParams(
                subsId, pub_key_id,
                payload,
                getPrivateKey("Ed25519", Base64.getDecoder().decode(privateKey))
        );
    }

    private String generateAuthorizationParams(String subscriberId, String pub_key_id, String payload,
                                              PrivateKey privateKey) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        //  payload = payload.replaceAll("\\s", "");
        Map<String, String> map = new HashMap<>();

        map.put("keyId", subscriberId + '|' + pub_key_id + '|' + "ed25519");
        map.put("algorithm", "ed25519");

        long created_at = System.currentTimeMillis() / 1000L;
        long expires_at = created_at + 10;

        map.put("created", Long.toString(created_at));
        map.put("expires", Long.toString(expires_at));
        map.put("headers", "(created) (expires) digest");
        map.put("signature",
                generateSignature(generateBlakeHash(getSigningString(created_at, expires_at, payload)), privateKey));
        return objectMapper.writeValueAsString(map);
    }

    private String generateSignature(String req, PrivateKey privateKey) {
        return generateSignature(req, SIGNATURE_ALGO, privateKey);
    }

    private String generateSignature(String payload, String signatureAlgorithm, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(signatureAlgorithm, provider); //
            signature.initSign(privateKey);
            signature.update(payload.getBytes());
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String generateBlakeHash(String req) {
        return toBase64(digest("BLAKE2B-512", req));
    }

    private String getSigningString(long created_at, long expires_at, String payload) {
        return "(created): " + created_at +
                " (expires): " + expires_at +
                " digest: BLAKE-512=" + hash(payload);

    }

    private String toBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private byte[] digest(String algorithm, String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm, provider);
            digest.reset();
            digest.update(payload.getBytes(StandardCharsets.UTF_8));
            return digest.digest();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static PrivateKey getPrivateKey(String algo, byte[] jceBytes) throws Exception {
        return KeyFactory.getInstance(algo, BouncyCastleProvider.PROVIDER_NAME)
                .generatePrivate(new PKCS8EncodedKeySpec(jceBytes));
    }

    private String hash(String payload) {
        return generateBlakeHash(payload);
    }
}

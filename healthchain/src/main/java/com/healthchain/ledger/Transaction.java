package com.healthchain.ledger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthchain.crypto.CryptoUtils;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class Transaction {
    private String recordId;
    private String dataHash;
    private String ownerId;
    private String receiverId;
    private long timestampMs;
    private String signatureB64;

    public Transaction() {}

    public Transaction(String recordId, String dataHash, String ownerId, String receiverId, long timestampMs) {
        this.recordId = recordId;
        this.dataHash = dataHash;
        this.ownerId = ownerId;
        this.receiverId = receiverId;
        this.timestampMs = timestampMs;
    }

    public String getRecordId() { return recordId; }
    public String getDataHash() { return dataHash; }
    public String getOwnerId() { return ownerId; }
    public String getReceiverId() { return receiverId; }
    public long getTimestampMs() { return timestampMs; }
    public String getSignatureB64() { return signatureB64; }

    public void setSignatureB64(String signatureB64) { this.signatureB64 = signatureB64; }

    @JsonIgnore
    public String dataToSign() {
        return recordId + "|" + dataHash + "|" + ownerId + "|" + receiverId + "|" + timestampMs;
    }

    public void sign(PrivateKey privateKey) {
        byte[] sig = CryptoUtils.sign(dataToSign().getBytes(StandardCharsets.UTF_8), privateKey);
        this.signatureB64 = Base64.getEncoder().encodeToString(sig);
    }

    public boolean verify(PublicKey ownerPublicKey) {
        if (signatureB64 == null) return false;
        byte[] sig = Base64.getDecoder().decode(signatureB64);
        return CryptoUtils.verify(dataToSign().getBytes(StandardCharsets.UTF_8), sig, ownerPublicKey);
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"serialization\"}";
        }
    }
}


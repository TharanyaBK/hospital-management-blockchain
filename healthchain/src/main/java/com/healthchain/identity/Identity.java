package com.healthchain.identity;

import com.healthchain.crypto.CryptoUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class Identity {
    private final String id;
    private final KeyPair keyPair;

    public Identity(String id) {
        this.id = id;
        this.keyPair = CryptoUtils.generateRsaKeyPair();
    }

    public String getId() {
        return id;
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(getPublicKey().getEncoded());
    }
}


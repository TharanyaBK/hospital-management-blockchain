package com.healthchain.crypto;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
    private static final String RSA_ALGO = "RSA";
    private static final String SIGN_ALGO = "SHA256withRSA";
    private static final String AES_ALGO = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";

    public static KeyPair generateRsaKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA_ALGO);
            kpg.initialize(2048);
            return kpg.generateKeyPair();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
    }

    public static byte[] sign(byte[] data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(SIGN_ALGO);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to sign data", e);
        }
    }

    public static boolean verify(byte[] data, byte[] sig, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance(SIGN_ALGO);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sig);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to verify signature", e);
        }
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static SecretKey generateAesKey(int bits) {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(AES_ALGO);
            kg.init(bits);
            return kg.generateKey();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to generate AES key", e);
        }
    }

    public static String encryptAesGcm(String plaintext, byte[] keyBytes, byte[] iv) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, AES_ALGO);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
            byte[] ct = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(ct);
        } catch (Exception e) {
            throw new RuntimeException("AES-GCM encryption failed", e);
        }
    }

    public static String decryptAesGcm(String ciphertextB64, byte[] keyBytes, byte[] iv) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, AES_ALGO);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
            byte[] pt = cipher.doFinal(Base64.getDecoder().decode(ciphertextB64));
            return new String(pt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES-GCM decryption failed", e);
        }
    }

    public static PublicKey decodeRsaPublicKey(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance(RSA_ALGO);
            return kf.generatePublic(spec);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to decode public key", e);
        }
    }
}


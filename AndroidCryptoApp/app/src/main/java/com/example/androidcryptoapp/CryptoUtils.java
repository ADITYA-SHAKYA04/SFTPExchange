package com.example.androidcryptoapp;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String AES_GCM_ALIAS = "aes_gcm_key";
    private static final String AES_CBC_ALIAS = "aes_cbc_key";
    private static final String RSA_ALIAS = "rsa_key";
    private static final int GCM_IV_LENGTH = 12;
    private static final int CBC_IV_LENGTH = 16;
    private static final int GCM_TAG_LENGTH = 128;

    public static String encrypt(String algorithm, String plainText) throws Exception {
        switch (algorithm) {
            case "AES-GCM":
                return encryptAesGcm(plainText);
            case "AES-CBC":
                return encryptAesCbc(plainText);
            case "RSA-OAEP":
                return encryptRsaOaep(plainText);
            default:
                throw new IllegalArgumentException("Unknown algorithm");
        }
    }
    public static String decrypt(String algorithm, String cipherText) throws Exception {
        switch (algorithm) {
            case "AES-GCM":
                return decryptAesGcm(cipherText);
            case "AES-CBC":
                return decryptAesCbc(cipherText);
            case "RSA-OAEP":
                return decryptRsaOaep(cipherText);
            default:
                throw new IllegalArgumentException("Unknown algorithm");
        }
    }
    // AES-GCM
    private static String encryptAesGcm(String plainText) throws Exception {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEYSTORE);
        ks.load(null);
        if (!ks.containsAlias(AES_GCM_ALIAS)) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            keyGenerator.init(new KeyGenParameterSpec.Builder(AES_GCM_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build());
            keyGenerator.generateKey();
        }
        SecretKey key = ((KeyStore.SecretKeyEntry) ks.getEntry(AES_GCM_ALIAS, null)).getSecretKey();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] ciphertext = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
        return Base64.encodeToString(combined, Base64.DEFAULT);
    }
    private static String decryptAesGcm(String cipherText) throws Exception {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEYSTORE);
        ks.load(null);
        SecretKey key = ((KeyStore.SecretKeyEntry) ks.getEntry(AES_GCM_ALIAS, null)).getSecretKey();
        byte[] combined = Base64.decode(cipherText, Base64.DEFAULT);
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] ciphertext = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);
        byte[] plain = cipher.doFinal(ciphertext);
        return new String(plain, StandardCharsets.UTF_8);
    }
    // AES-CBC
    private static String encryptAesCbc(String plainText) throws Exception {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEYSTORE);
        ks.load(null);
        if (!ks.containsAlias(AES_CBC_ALIAS)) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            keyGenerator.init(new KeyGenParameterSpec.Builder(AES_CBC_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setKeySize(256)
                    .build());
            keyGenerator.generateKey();
        }
        SecretKey key = ((KeyStore.SecretKeyEntry) ks.getEntry(AES_CBC_ALIAS, null)).getSecretKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        byte[] iv = new byte[CBC_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] ciphertext = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
        return Base64.encodeToString(combined, Base64.DEFAULT);
    }
    private static String decryptAesCbc(String cipherText) throws Exception {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEYSTORE);
        ks.load(null);
        SecretKey key = ((KeyStore.SecretKeyEntry) ks.getEntry(AES_CBC_ALIAS, null)).getSecretKey();
        byte[] combined = Base64.decode(cipherText, Base64.DEFAULT);
        byte[] iv = new byte[CBC_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, CBC_IV_LENGTH);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] ciphertext = new byte[combined.length - CBC_IV_LENGTH];
        System.arraycopy(combined, CBC_IV_LENGTH, ciphertext, 0, ciphertext.length);
        byte[] plain = cipher.doFinal(ciphertext);
        return new String(plain, StandardCharsets.UTF_8);
    }
    // RSA-OAEP
    private static String encryptRsaOaep(String plainText) throws Exception {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEYSTORE);
        ks.load(null);
        if (!ks.containsAlias(RSA_ALIAS)) {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE);
            kpg.initialize(new KeyGenParameterSpec.Builder(RSA_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setKeySize(2048)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                    .build());
            kpg.generateKeyPair();
        }
        PublicKey publicKey = ks.getCertificate(RSA_ALIAS).getPublicKey();
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] ciphertext = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(ciphertext, Base64.DEFAULT);
    }
    private static String decryptRsaOaep(String cipherText) throws Exception {
        KeyStore ks = KeyStore.getInstance(ANDROID_KEYSTORE);
        ks.load(null);
        PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) ks.getEntry(RSA_ALIAS, null)).getPrivateKey();
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] ciphertext = Base64.decode(cipherText, Base64.DEFAULT);
        byte[] plain = cipher.doFinal(ciphertext);
        return new String(plain, StandardCharsets.UTF_8);
    }
}

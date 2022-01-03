package com.emedicoz.app.utilso;

import android.annotation.SuppressLint;
import android.util.Base64;

import com.emedicoz.app.BuildConfig;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@SuppressLint("NewApi")
public class AES {

    public static String strArrayKey = "!*@#)($^%1fgv&C=";
    public static String strArrayvector = "?\\:><{}@#Vjekl/4";
    private static String CIPHER_NAME = "AES/CBC/NoPadding";
    private static int CIPHER_KEY_LEN = 16; //128 bits

    /**
     * Encrypt data using AES Cipher (CBC) with 128 bit key
     *
     * @param key  - key to use should be 16 bytes long (128 bits)
     * @param iv   - initialization vector
     * @param data - data to encrypt
     * @return encryptedData data in base64 encoding with iv attached at end after a :
     */
    public static String encrypt(String key, String iv, String data) {

        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKey = new SecretKeySpec(fixKey(key).getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(AES.CIPHER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] encryptedData = cipher.doFinal((data.getBytes()));
            String encryptedDataInBase64 = Base64.encodeToString(encryptedData, Base64.DEFAULT);
            String ivInBase64 = Base64.encodeToString(iv.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);

            return encryptedDataInBase64 + ":" + ivInBase64;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String fixKey(String key) {

        if (key.length() < AES.CIPHER_KEY_LEN) {
            int numPad = AES.CIPHER_KEY_LEN - key.length();

            StringBuilder keyBuilder = new StringBuilder(key);
            for (int i = 0; i < numPad; i++) {
                keyBuilder.append("0"); //0 pad to len 16 bytes
            }
            key = keyBuilder.toString();

            return key;

        }

        if (key.length() > AES.CIPHER_KEY_LEN) {
            return key.substring(0, CIPHER_KEY_LEN); //truncate to 16 bytes
        }

        return key;
    }

    /**
     * Decrypt data using AES Cipher (CBC) with 128 bit key
     *
     * @param key  - key to use should be 16 bytes long (128 bits)
     * @param data - encrypted data with iv at the end separate by :
     * @return decrypted data string
     */

    public static String decrypt(String data, String key, String ivParameter) {

        try {
            String[] parts = data.split(":");

            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(AES.CIPHER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            byte[] decodedEncryptedData = Base64.decode(parts[0], Base64.NO_PADDING);

            byte[] original = cipher.doFinal(decodedEncryptedData);

            return new String(original);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String generatekey(String token) {
        StringBuilder finalKey = new StringBuilder();
        String[] parts = token.split("_");
        for (char c : parts[2].toCharArray()) {
            finalKey.append(strArrayKey.toCharArray()[Integer.parseInt(String.valueOf(c))]);
        }
        return finalKey.toString();
    }

    public static String generateVector(String token) {
        StringBuilder finalKey = new StringBuilder();
        String[] parts = token.split("_");
        for (char c : parts[2].toCharArray()) {
            finalKey.append(strArrayvector.toCharArray()[Integer.parseInt(String.valueOf(c))]);
        }
        return finalKey.toString();
    }

//  Updated Methods


    private static Cipher cipher = null;

    static {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(BuildConfig.ENC_IV_KEY.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(BuildConfig.ENC_SECRET_KEY.getBytes("UTF-8"), "AES");

            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return tech.gusavila92.apache.commons.codec.binary.Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encrypted) {
        if (encrypted.contains("http") || encrypted.contains(".mp4"))
            return encrypted;
        try {
            IvParameterSpec iv = new IvParameterSpec(BuildConfig.ENC_IV_KEY.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(BuildConfig.ENC_SECRET_KEY.getBytes("UTF-8"), "AES");

            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(tech.gusavila92.apache.commons.codec.binary.Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}

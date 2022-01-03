package com.emedicoz.app.utilso;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryprDecriptManager {

    static SecretKey yourKey = null;
    private static String algorithm = "AES";


    public static byte[] getVideoFile(String filPath) throws FileNotFoundException {
        byte[] audio_data = null;
        byte[] inarry = null;

        try {
            File initialFile = new File(filPath);
            InputStream is = new FileInputStream(initialFile);// use recorded file instead of getting file from assets folder.
            int length = is.available();
            audio_data = new byte[length];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = is.read(audio_data)) != -1) {
                output.write(audio_data, 0, bytesRead);
            }
            inarry = output.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return inarry;

    }

    public static void saveFile(byte[] stringToSave, File mFile) {

        try {
            File file = mFile;

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            yourKey = generateKey();
            byte[] filesBytes = encodeFile(yourKey, stringToSave);
            bos.write(filesBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static byte[] encodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] encrypted = null;
        byte[] data = yourKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        encrypted = cipher.doFinal(fileData);
        return encrypted;
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key

        String json = SharedPreference.getInstance().getString("SecretKey");

        if (!"".equalsIgnoreCase(json)) {

            byte[] encodedKey = Base64.decode(json, Base64.DEFAULT);
            SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
            return originalKey;
        }

        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        yourKey = keyGenerator.generateKey();

        String stringKey = "";

        if (yourKey != null) {
            stringKey = Base64.encodeToString(yourKey.getEncoded(), Base64.DEFAULT);
        }

        Log.d("URL", "secretJSON" + stringKey);

        SharedPreference.getInstance().putString("SecretKey", stringKey);

        return yourKey;
    }

    public static byte[] decodeMFile(SecretKey yourKey, File file) {

        byte[] decodedData = null;
        try {
            decodedData = decodeFile(yourKey, readFile(file));
            // String str = new String(decodedData);
            //System.out.println("DECODED FILE CONTENTS : " + str);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return decodedData;
        }
    }

    public static byte[] decodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, yourKey, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        decrypted = cipher.doFinal(fileData);


        return decrypted;
    }

    public static byte[] readFile(File file) {
        byte[] contents = null;

        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

}

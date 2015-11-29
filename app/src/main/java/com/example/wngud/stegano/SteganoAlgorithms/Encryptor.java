
package com.example.wngud.stegano.SteganoAlgorithms;

import android.util.Log;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;


public class Encryptor {

    public static byte[] encryptAES(byte[] toEncrypt, byte[] key)
    {
        byte[] toReturn = null;
        byte[] paddedKey = new byte[16];
        Arrays.fill(paddedKey, (byte)0);
        int keyLen = key.length > paddedKey.length ? paddedKey.length : key.length;
        System.arraycopy(key, 0, paddedKey, 0, keyLen);
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec skc = new SecretKeySpec(paddedKey, "AES");

            cipher.init(Cipher.ENCRYPT_MODE, skc);
            toReturn = cipher.doFinal(toEncrypt);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    return toReturn;
    }

    public static byte[] decryptAES(byte[] toDecrypt, byte[] key)
    {
        byte[] decrypted = null;
        byte[] paddedKey = new byte[16];
        Arrays.fill(paddedKey, (byte)0);
        int keyLen = key.length > paddedKey.length ? paddedKey.length : key.length;
        System.arraycopy(key, 0, paddedKey, 0, keyLen);
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec skc = new SecretKeySpec(paddedKey, "AES");

            cipher.init(Cipher.DECRYPT_MODE, skc);
            decrypted = cipher.doFinal(toDecrypt);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return decrypted;
    }

    public static byte[] encryptBlowfish(byte[] toEncrypt, byte[] key) throws Exception {
        // create a binary key from the argument key (seed)
        SecretKey blowfishKey = new SecretKeySpec(key, "Blowfish");
        // create an instance of cipher
        Cipher cipher = Cipher.getInstance("blowfish");

        // initialize the cipher with the key
        cipher.init(Cipher.ENCRYPT_MODE, blowfishKey);

        // enctypt!
        return cipher.doFinal(toEncrypt);
    }

    public static byte[] decryptBlowfish(byte[] toDecrypt, byte[] key) throws Exception {
        // create a binary key from the argument key (seed)

        SecretKey blowfishKey = new SecretKeySpec(key, "Blowfish");

        // do the decryption with that key
        Cipher cipher = Cipher.getInstance("blowfish");
        cipher.init(Cipher.DECRYPT_MODE, blowfishKey);

        return cipher.doFinal(toDecrypt);
    }

    public static byte[] encryptDES(byte[] toEncrypt, byte[] key) throws Exception {
        // create a binary key from the argument key (seed)
        byte[] paddedKey = new byte[8];

        Arrays.fill(paddedKey, (byte)0);
        int keyLen = key.length > paddedKey.length ? paddedKey.length : key.length;
        System.arraycopy(key, 0, paddedKey, 0, keyLen);
        Log.d("encryptor", String.valueOf(paddedKey.length));
        // create an instance of cipher
        Cipher cipher = Cipher.getInstance("DES");

        // initialize the cipher with the key
        SecretKeySpec skc = new SecretKeySpec(paddedKey, "DES");
        cipher.init(Cipher.ENCRYPT_MODE, skc);

        // enctypt!
        return cipher.doFinal(toEncrypt);
    }

    public static byte[] decryptDES(byte[] toDecrypt, byte[] key) throws Exception {
        // create a binary key from the argument key (seed)
        byte[] paddedKey = new byte[8];
        Arrays.fill(paddedKey, (byte)0);
        int keyLen = key.length > paddedKey.length ? paddedKey.length : key.length;
        System.arraycopy(key, 0, paddedKey, 0, keyLen);
        // create an instance of cipher

        // initialize the cipher with the key
        SecretKeySpec skc = new SecretKeySpec(paddedKey, "DES");
        // do the decryption with that key
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, skc);
        return cipher.doFinal(toDecrypt);
    }
}


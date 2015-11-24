/**
 * Created by Spag on 2015-11-02.
 */
package com.example.wngud.stegano.SteganoAlgorithms;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
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
            System.exit(1);
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
            System.exit(1);
        }
        return decrypted;
    }

    public static byte[] encryptBlowfish(byte[] toEncrypt, byte[] key) throws Exception {
        // create a binary key from the argument key (seed)
        SecureRandom sr = new SecureRandom(key);
        KeyGenerator kg = KeyGenerator.getInstance("blowfish");
        kg.init(sr);
        SecretKey sk = kg.generateKey();

        // create an instance of cipher
        Cipher cipher = Cipher.getInstance("blowfish");

        // initialize the cipher with the key
        cipher.init(Cipher.ENCRYPT_MODE, sk);

        // enctypt!
        byte[] encrypted = cipher.doFinal(toEncrypt);

        return encrypted;
    }

    public static byte[] decryptBlowfish(byte[] toDecrypt, byte[] key) throws Exception {
        // create a binary key from the argument key (seed)
        SecureRandom sr = new SecureRandom(key);
        KeyGenerator kg = KeyGenerator.getInstance("blowfish");
        kg.init(sr);
        SecretKey sk = kg.generateKey();

        // do the decryption with that key
        Cipher cipher = Cipher.getInstance("blowfish");
        cipher.init(Cipher.DECRYPT_MODE, sk);
        byte[] decrypted = cipher.doFinal(toDecrypt);

        return decrypted;
    }

    public static byte[] encryptDES(byte[] toEncrypt, byte[] key) throws Exception {
        // create a binary key from the argument key (seed)
        SecureRandom sr = new SecureRandom(key);
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(sr);
        SecretKey sk = kg.generateKey();

        // create an instance of cipher
        Cipher cipher = Cipher.getInstance("DES");

        // initialize the cipher with the key
        cipher.init(Cipher.ENCRYPT_MODE, sk);

        // enctypt!
        byte[] encrypted = cipher.doFinal(toEncrypt);

        return encrypted;
    }

    public static byte[] decryptDES(byte[] toDecrypt, byte[] key) throws Exception {
        // create a binary key from the argument key (seed)
        SecureRandom sr = new SecureRandom(key);
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(sr);
        SecretKey sk = kg.generateKey();

        // do the decryption with that key
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, sk);
        byte[] decrypted = cipher.doFinal(toDecrypt);

        return decrypted;
    }
}


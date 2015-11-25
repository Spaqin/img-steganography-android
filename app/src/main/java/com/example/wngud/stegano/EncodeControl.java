package com.example.wngud.stegano;

import android.graphics.Bitmap;
import android.util.Log;
import com.example.wngud.stegano.SteganoAlgorithms.*;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;


public class EncodeControl {
    private final int BITS_PER_COLOR = 2;
    private Encryption_type encType;
    private String filepath;
    private String message;
    private String password;
    private Bitmap picture;
    private boolean isText = true;
    private String key;

    public void setEncType(Encryption_type enct)
    {
        encType = enct;
    }

    public void setPassword(String passwd)
    {
        password = passwd;
    }

    public void setFilepath(String fp)
    {
        filepath = fp;
    }

    public void setMessage(String msg) {
        message = msg;
    }

    public void setIsText(boolean isTxt)
    {
        isText = isTxt;
    }

    public void setKey(String k)
    {
        key = k;
    }
    public void setPicture(Bitmap pic)
    {
        picture = pic;
        Log.d("[IMG STG]", picture.getHeight() + " x " + picture.getWidth());
    }

    /** The premise is this: this method returns a bitmap,
     *      which is then displayed in the UI and the user can decide what to do with it: save it to external storage, share it, remove it.
     * @return Encoded bitmap.
     * @throws Exception - all the exceptions are here. I don't feel like writing every one of them.
     */
    public Bitmap encode() throws Exception
    {
        byte[] data;
        int[] pixels = null; //warnings say that this value is always null, but it is actually set by the following line:
        picture.getPixels(pixels, 0, picture.getWidth(), 0, 0, picture.getWidth(), picture.getHeight());
        HideInformation hi;

        //if(isText)
        {
            data = message.getBytes(StandardCharsets.UTF_8);

        }
        //else
        {
            //todo implement file support
        }

        switch (encType) {
            case NONE:
                Log.d("EncodeControl", "No encryption");
                break;
            case AES:
                Log.d("EncodeControl", "AES encryption");
                data = Encryptor.encryptAES(data, key.getBytes(StandardCharsets.UTF_8));
                Log.d("EncodeControl", "Encrypted: " + new String(data, StandardCharsets.UTF_8));
                break;
            case DES:
                Log.d("EncodeControl", "DES encryption");
                data = Encryptor.encryptDES(data, key.getBytes(StandardCharsets.UTF_8));
                Log.d("EncodeControl", "Encrypted: " + new String(data, StandardCharsets.UTF_8));
                break;
            case BLOWFISH:
                Log.d("EncodeControl", "BlowFish encryption");
                data = Encryptor.encryptBlowfish(data, key.getBytes(StandardCharsets.UTF_8));
                Log.d("EncodeControl", "Encrypted: " + new String(data, StandardCharsets.UTF_8));
                break;
        }
        if(isText)
        {
            Log.d("EncodeControl", "Encoding text...");
            hi = new HideInformation(pixels, data);
        }
        else
        {
            Log.d("EncodeControl", "Encoding file...");
            hi = new HideInformation(pixels, data, filepath);
        }
        picture.setPixels(hi.encodeData(BITS_PER_COLOR), 0, picture.getWidth(), 0, 0, picture.getWidth(), picture.getHeight());
        Log.d("EncodeControl", "Picture is set.");
        return picture;

    }
}

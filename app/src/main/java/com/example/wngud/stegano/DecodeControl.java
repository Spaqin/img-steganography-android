package com.example.wngud.stegano;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.wngud.stegano.SteganoAlgorithms.Encryptor;

import com.example.wngud.stegano.SteganoAlgorithms.UnhideInformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DecodeControl{
    private final int BITS_PER_COLOR = 2;
    private final static String LOG_TAG = "DecodeControl";
    private final static String FOLDER_NAME = "Steganography";
    private Encryption_type encType;
    private Bitmap picture;
    private boolean isText;
    private String key;
    private Context context;
    private File f;

    public DecodeControl(Context context)
    {
        this.context = context;
        encType = Encryption_type.NONE;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setEncType(Encryption_type encType) {
        this.encType = encType;
    }

    public boolean isText() {
        return isText;
    }

    public String decode()
    {
        byte[] data;
        int[] pixels = new int[picture.getWidth()*picture.getHeight()];
        long tStart, tCheckpoint, tFinish;

        tStart = System.currentTimeMillis();

        picture.getPixels(pixels, 0, picture.getWidth(), 0, 0, picture.getWidth(), picture.getHeight());

        tCheckpoint = System.currentTimeMillis();
        Log.d(LOG_TAG, "Getting pixels: " + String.valueOf(tCheckpoint - tStart) + "ms");

        UnhideInformation unhi = new UnhideInformation(pixels, BITS_PER_COLOR);

        Log.d(LOG_TAG, "Decoding: " + String.valueOf(System.currentTimeMillis() - tCheckpoint)  + "ms");
        tCheckpoint = System.currentTimeMillis();
        data = unhi.getInfo();
        try {
            switch (encType) {
                case NONE:
                    Log.d(LOG_TAG, "No encryption");
                    Log.d(LOG_TAG, "Decrypted: " + new String(data, StandardCharsets.UTF_8));
                    break;
                case AES:
                    Log.d(LOG_TAG, "AES encryption");
                    data = Encryptor.decryptAES(data, key.getBytes(StandardCharsets.UTF_8));
                    Log.d(LOG_TAG, "Decrypted: " + new String(data, StandardCharsets.UTF_8));
                    break;
                case DES:
                    Log.d(LOG_TAG, "DES encryption");
                    data = Encryptor.decryptDES(data, key.getBytes(StandardCharsets.UTF_8));
                    Log.d(LOG_TAG, "Decrypted: " + new String(data, StandardCharsets.UTF_8));
                    break;
                case BLOWFISH:
                    Log.d(LOG_TAG, "BlowFish encryption");
                    data = Encryptor.decryptBlowfish(data, key.getBytes(StandardCharsets.UTF_8));
                    Log.d(LOG_TAG, "Decrypted: " + new String(data, StandardCharsets.UTF_8));
                    break;

            }
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "Error with decryption! ", e);
            Toast.makeText(context, context.getString(R.string.error_decode_encryption), Toast.LENGTH_LONG).show();
        }
        Log.d(LOG_TAG, "Decryption: " + String.valueOf(System.currentTimeMillis() - tCheckpoint)  + "ms");
        tFinish = System.currentTimeMillis();
        Log.d(LOG_TAG, "Total: " + String.valueOf(tFinish - tStart)  + "ms");
        isText = unhi.getTextOnly();
        if(isText) //is text, so message is returned
        {
            return new String(data, StandardCharsets.UTF_8);
        }
        else
        {
            //handle file creation
            Toast.makeText(context, R.string.file_found_toast, Toast.LENGTH_LONG).show();
            String extension = unhi.getFileExtension();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String filename = dateFormat.format(new Date());
            f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + FOLDER_NAME, filename + "." + extension);
            try {
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(data);
                Toast.makeText(context, R.string.file_saved_toast + " " + f.getPath(), Toast.LENGTH_LONG).show();
            }
            catch (IOException e)
            {
                Toast.makeText(context, R.string.error_file_saved, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        return null;
    }



}

package com.example.wngud.stegano;

import android.graphics.Bitmap;
import android.util.Log;
import com.example.wngud.stegano.SteganoAlgorithms.*;


public class EncodeControl {
    private Encryption_type encType;
    private String filepath;
    private String message;
    private String password;
    private Bitmap picture;

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

    public void setPicture(Bitmap pic)
    {
        picture = pic;
        Log.d("[IMG STG]", picture.getHeight() + " x " + picture.getWidth());
    }

    public void encode()
    {
       // HideInformation hi = new HideInformation()
    }
}

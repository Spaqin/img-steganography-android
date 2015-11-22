package com.example.wngud.stegano;

import android.graphics.Bitmap;


public class EncodeControl {
    private Encryption_type encType;
    private String filename;
    private String password;
    private Bitmap picture;

    public void setEncType(Encryption_type enct)
    {
        encType = enct;
    }

    public void setFilename(String fn)
    {
        filename = fn;
    }

    public void setPassword(String passwd)
    {
        password = passwd;
    }

    public void encode()
    {

    }
}

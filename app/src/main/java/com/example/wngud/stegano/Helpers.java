package com.example.wngud.stegano;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Helpers {

    private final static String LOG_TAG = "Helpers";
    private final static String FOLDER_NAME = "Steganography";
    public static Bitmap bitmap;
    public static Bitmap decodeBitmap;
    /**
     * Since max texture size is 4Kx4K, we have to size it down. Also, no new phones have screens wider than 1440px, but there will be no difference for 1080p width, so we resize to that
     * @param toResize - bitmap to resize
     * @return resized bitmap
     */
    public static Bitmap resizeForPreview(Bitmap toResize)
    {
        final int maxSize = 1024;
        int width = toResize.getWidth();
        int height = toResize.getHeight();
        int newWidth = width, newHeight = height;

        if(width > height && width > maxSize)
        {
            newWidth = maxSize;
            newHeight = (height * maxSize) / width;
        }
        else if(height > maxSize)
        {
            newHeight = maxSize;
            newWidth = (width * maxSize) / height;
        }

        return Bitmap.createScaledBitmap(toResize, newWidth, newHeight, false);
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    private static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStorageDirectory(), albumName);
        if (!file.mkdirs()) {
            Log.d(LOG_TAG, "Directory not created");
        }
        return file;
    }

    public static File saveText(String text) throws IOException
    {
        File f;
        String filename;
        if(!isExternalStorageWritable())
            throw new IOException("Storage not available");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        filename = dateFormat.format(new Date());

        f = new File(getAlbumStorageDir(FOLDER_NAME), "Message " + filename + ".txt");
        FileWriter fw = new FileWriter(f);
        fw.write(text);
        fw.close();
        return f;
    }



}

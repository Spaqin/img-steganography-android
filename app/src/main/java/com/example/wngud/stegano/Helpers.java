package com.example.wngud.stegano;

import android.graphics.Bitmap;

/**
 * Created by Spag on 2015-11-24.
 */
public class Helpers {

    /**
     * Since max texture size is 4Kx4K, we have to size it down. Also, no new phones have screens wider than 1440px, but there will be no difference for 1080p width, so we resize to that
     * @param toResize - bitmap to resize
     * @return resized bitmap
     */
    public static Bitmap resizeForPreview(Bitmap toResize)
    {
        final int maxSize = 1080;
        int width = toResize.getWidth();
        int height = toResize.getHeight();
        int newWidth = width, newHeight = height;

        if(width > height && width < maxSize)
        {
            newWidth = maxSize;
            newHeight = (height * maxSize) / width;
        }
        else if(height < maxSize)
        {
            newHeight = maxSize;
            newWidth = (width * maxSize) / height;
        }

        return Bitmap.createScaledBitmap(toResize, newWidth, newHeight, false);
    }

}

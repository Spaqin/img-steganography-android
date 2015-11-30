package com.example.wngud.stegano;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.wngud.stegano.SteganoAlgorithms.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.NoSuchPaddingException;


public class EncodeControl extends AsyncTask<String, String, Boolean> {
    private final int BITS_PER_COLOR = 2;
    private final static String LOG_TAG = "EncodeControl";
    private final static String FOLDER_NAME = "Steganography";
    private ProgressDialog progressDialog;
    private Encryption_type encType;
    private String filepath;
    private String message;
    private Bitmap picture;
    private boolean isText = true;
    private String key;
    private Context context;
    private Activity activity;
    private boolean ok;
    private File f;
    private Uri mImageCaptureUri;

    public EncodeControl(Activity activity)
    {
        this.activity = activity;
        this.context = activity;
        progressDialog = new ProgressDialog(context);
        encType = Encryption_type.NONE;
    }

    public void setEncType(Encryption_type enct)
    {
        encType = enct;
    }

    public void setFilepath(String fp)
    {
        filepath = fp;
    }

    public void setMessage(String msg) {
        message = msg;
        Log.d(LOG_TAG,"Message: " + message);
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
        picture = pic.copy(Bitmap.Config.ARGB_8888, true);
        Log.d("[IMG STG]", picture.getHeight() + " x " + picture.getWidth());
    }


    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
   private File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStorageDirectory(), albumName);
        if (!file.mkdirs()) {
            Log.d(LOG_TAG, "Directory not created");
        }
        return file;
    }

    private void save(@Nullable String filename) throws IOException
    {
        if(!isExternalStorageWritable())
            throw new IOException("Storage not available");
        if(filename == null || filename.isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            filename = dateFormat.format(new Date());
        }
        f = new File(getAlbumStorageDir(FOLDER_NAME), filename+".png");
        FileOutputStream savedStream = new FileOutputStream(f);
        picture.compress(Bitmap.CompressFormat.PNG, 100, savedStream);
        savedStream.close();
        //make it show up in gallery:
        new SingleMediaScanner(context, f);


    }


    protected void onPreExecute()
    {
        progressDialog.setMessage("Getting pixels...");
        progressDialog.setTitle("Please wait");
        progressDialog.show();
    }

    protected void onProgressUpdate(String... progress)
    {
        progressDialog.setMessage(progress[0]);
        progressDialog.show();
    }
    protected void onPostExecute(final Boolean success)
    {
        if(progressDialog.isShowing())
            progressDialog.dismiss();
        if(!success) {
            Toast.makeText(context, context.getString(R.string.error_encode), Toast.LENGTH_SHORT).show();
            ok = false;
        }
        else {
            Toast.makeText(context, context.getString(R.string.picture_path) + " " + f.getPath(), Toast.LENGTH_LONG).show();
            ok = true;
        }
        mImageCaptureUri = Uri.fromFile(f);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Share");
        builder.setMessage("Do you want to open Gallery and share?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e(LOG_TAG, "mImageCaptureUri = " + mImageCaptureUri);
                //sendMMS(mImageCaptureUri);
                sendMMS();

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void sendMMS(){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setDataAndType(mImageCaptureUri, "image/png");

        /*sendIntent.addCategory("android.intent.category.DEFAULT");
        sendIntent.addCategory("android.intent.category.BROWSABLE");
        sendIntent.putExtra("address", "01000000000");
        sendIntent.putExtra("exit_on_sent", true);
        sendIntent.putExtra("subject", "dfdfdf");
        sendIntent.putExtra("sms_body", "dfdfsdf");
        Uri dataUri = Uri.parse("" + mImageCaptureUri);
        sendIntent.putExtra(Intent.EXTRA_STREAM, dataUri);

        **I'm sorry I reduced all your effort to two lines :(
        */

        context.startActivity(sendIntent);
    }

    public boolean wasSuccess(){
        return ok;
    }
    /** The premise is this: this method does everything in the background - calculations and saving the file,
     *      picture is then displayed in the UI and the user can decide what to do with it: save it to external storage, share it, remove it.
     * @return Whether the operation was a success or not
     */
    protected Boolean doInBackground(String... filename)
    {
        byte[] data;
        int[] pixels = new int[picture.getWidth()*picture.getHeight()];
        long tStart, tCheckpoint, tFinish;
        tStart = System.currentTimeMillis();
        picture.getPixels(pixels, 0, picture.getWidth(), 0, 0, picture.getWidth(), picture.getHeight());
        tCheckpoint = System.currentTimeMillis();
        Log.d(LOG_TAG, "Getting pixels: " + String.valueOf(tCheckpoint - tStart)  + "ms");
        HideInformation hi;

        publishProgress("Encrypting...");
        //if(isText)
        {
            data = message.getBytes(StandardCharsets.UTF_8);

        }
        //else
        {
            //todo implement file support
        }
        try {
        switch (encType) {
            case NONE:
                Log.d(LOG_TAG, "No encryption");
                break;
            case AES:
                Log.d(LOG_TAG, "AES encryption");
                data = Encryptor.encryptAES(data, key.getBytes(StandardCharsets.UTF_8));
                Log.d(LOG_TAG, "Encrypted: " + new String(data, StandardCharsets.UTF_8));
                break;
            case DES:
                Log.d(LOG_TAG, "DES encryption");
                data = Encryptor.encryptDES(data, key.getBytes(StandardCharsets.UTF_8));
                Log.d(LOG_TAG, "Encrypted: " + new String(data, StandardCharsets.UTF_8));
                break;
            case BLOWFISH:
                Log.d(LOG_TAG, "BlowFish encryption");
                data = Encryptor.encryptBlowfish(data, key.getBytes(StandardCharsets.UTF_8));
                Log.d(LOG_TAG, "Encrypted: " + new String(data, StandardCharsets.UTF_8));
                break;

        }

            Log.d(LOG_TAG, "Encrypting: " + String.valueOf(System.currentTimeMillis() - tCheckpoint)  + "ms");
            tCheckpoint = System.currentTimeMillis();
            publishProgress("Encoding...");
            if(isText)
            {
                Log.d(LOG_TAG, "Encoding text...");
                hi = new HideInformation(pixels, data);

            }
            else
            {
                Log.d(LOG_TAG, "Encoding file...");
                hi = new HideInformation(pixels, data, filepath);
            }
            Log.d(LOG_TAG, "Encoding: " + String.valueOf(System.currentTimeMillis() - tCheckpoint)  + "ms");
            tCheckpoint = System.currentTimeMillis();

            picture.setPixels(hi.encodeData(BITS_PER_COLOR), 0, picture.getWidth(), 0, 0, picture.getWidth(), picture.getHeight());
            Log.d(LOG_TAG, "Setting pixels: " + String.valueOf(System.currentTimeMillis() - tCheckpoint) + "ms");
            tCheckpoint = System.currentTimeMillis();
            Log.d(LOG_TAG, "Picture is set.");
            publishProgress("Saving...");
            save(filename[0]);
            tFinish = System.currentTimeMillis();
            Log.d(LOG_TAG, "Saving: " + String.valueOf(tFinish - tCheckpoint) + "ms");


            Log.d(LOG_TAG, "Total: " + String.valueOf(tFinish - tStart) + "ms");
        }
        catch(Exception e)
        {

            e.printStackTrace();
            return false;
        }
        return true;
    }
}

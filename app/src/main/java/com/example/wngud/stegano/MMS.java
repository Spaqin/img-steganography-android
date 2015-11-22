package com.example.wngud.stegano;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MMS extends AppCompatActivity {
    private static final int PICK_FROM_ALBUM = 0;
    private Uri mImageCaptureUri;
    private static final String TAG = "TestImageCropActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }
    public void onclick_mmsgallery(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public void onclick_mms(View v) {
        Log.e(TAG, "mImageCaptureUri = " + mImageCaptureUri);
        //sendMMS(mImageCaptureUri);
        sendMMS();
    }
    public void onclick_sns(View v) {
        Log.e(TAG, "mImageCaptureUri = " + mImageCaptureUri);
        //sendMMS(mImageCaptureUri);
        sendSNS();
    }
    private void sendMMS(Uri uri){
        uri = Uri.parse(""+uri);
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra("sms_body", "some text");
        it.putExtra(Intent.EXTRA_STREAM, uri);
        it.setType("image/*");
        // 삼성 단말에서만 허용 ( 앱 선택 박스 없이 호출 )
//      it.setComponent(new ComponentName("com.sec.mms", "com.sec.mms.Mms"));
        startActivity(it);
    }

    /**
     * MMS 발송 ( 첨부 파일 없음 )
     */
    private void sendMMS(){
        Uri mmsUri = Uri.parse("mmsto:");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, mmsUri);
        sendIntent.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://"));
        sendIntent.setType("image/jpg");

        sendIntent.addCategory("android.intent.category.DEFAULT");
        sendIntent.addCategory("android.intent.category.BROWSABLE");
        sendIntent.putExtra("address", "01000000000");
        sendIntent.putExtra("exit_on_sent", true);
        sendIntent.putExtra("subject", "dfdfdf");
        sendIntent.putExtra("sms_body", "dfdfsdf");
        Uri dataUri = Uri.parse("" + mImageCaptureUri);
        sendIntent.putExtra(Intent.EXTRA_STREAM, dataUri);

        startActivity(sendIntent);
    }
    private void sendSNS(){
        Uri mmsUri = Uri.parse("mmsto:");
        Intent sendIntent = new Intent(Intent.ACTION_SEND, mmsUri);
        sendIntent.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://"));
        sendIntent.setType("image/jpg");

        sendIntent.addCategory("android.intent.category.DEFAULT");
        sendIntent.addCategory("android.intent.category.BROWSABLE");
        sendIntent.putExtra("address", "01000000000");
        sendIntent.putExtra("exit_on_sent", true);
        sendIntent.putExtra("subject", "dfdfdf");
        sendIntent.putExtra("sms_body", "dfdfsdf");
        Uri dataUri = Uri.parse("" + mImageCaptureUri);
        sendIntent.putExtra(Intent.EXTRA_STREAM, dataUri);

        startActivity(sendIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {

            if(requestCode == PICK_FROM_ALBUM)
            {
                try {
                    ImageView imageView1 = (ImageView) findViewById(R.id.mmsimage);
                    mImageCaptureUri=data.getData();
                    imageView1.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(),mImageCaptureUri));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}

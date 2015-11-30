package com.example.wngud.stegano;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.zip.Inflater;
import android.support.v4.app.Fragment;

public class Encode extends AppCompatActivity {

    private EditText mPassField;
    private EditText mFileField;
    private EditText mMessageField;
    private Bitmap image;
    private EncodeControl encodeControl;
    private File tempFile;
    private Uri cameraUri;
    private static final String TAG = "TestImageCropActivity";


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ON CREATE", "this happened");
        setContentView(R.layout.activity_encode);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }
    public void onclick_camera(View v){
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        try {
            tempFile = File.createTempFile("temp", ".jpg", getExternalCacheDir());
            cameraUri = Uri.fromFile(tempFile);
        }
        catch (IOException e)
        {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.error_camera, Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }
    public void onclick_gallery(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public void onClickNoEncryption(View v)
    {
        mPassField = (EditText) findViewById(R.id.passwdField);
        mPassField.setVisibility(EditText.INVISIBLE);
    }
    public void onClickAES(View v)
    {
        mPassField = (EditText) findViewById(R.id.passwdField);
        mPassField.setVisibility(EditText.VISIBLE);
    }
    public void onClickDES(View v)
    {
        mPassField = (EditText) findViewById(R.id.passwdField);
        mPassField.setVisibility(EditText.VISIBLE);
    }
    public void onClickBlowFish(View v)
    {
        mPassField = (EditText) findViewById(R.id.passwdField);
        mPassField.setVisibility(EditText.VISIBLE);
    }

    public void onClickEncode(View v)
    {
        encodeControl = new EncodeControl(this);
        RadioButton mNoneRadio = (RadioButton) findViewById(R.id.noneRadio);
        RadioButton mAESRadio = (RadioButton) findViewById(R.id.AESRadio);
        RadioButton mDESRadio = (RadioButton) findViewById(R.id.DESRadio);
        mPassField = (EditText) findViewById(R.id.passwdField);
        mFileField = (EditText) findViewById(R.id.filenameField);
        mMessageField = (EditText) findViewById(R.id.messageField);


        if(mNoneRadio.isChecked())
            encodeControl.setEncType(Encryption_type.NONE);
        else if(mAESRadio.isChecked())
            encodeControl.setEncType(Encryption_type.AES);
        else if(mDESRadio.isChecked())
            encodeControl.setEncType(Encryption_type.DES);
        else
            encodeControl.setEncType(Encryption_type.BLOWFISH);

        encodeControl.setPicture(image);
        encodeControl.setKey(mPassField.getText().toString());
        encodeControl.setMessage(mMessageField.getText().toString());

        try {
            encodeControl.execute(mFileField.getText().toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {

            if (requestCode == PICK_FROM_CAMERA) // 1 은 위에서 startActivityForResult(intent, 1);
            {
                Bitmap resizedBmp;
                ContentResolver cr = getContentResolver();
                try {
                    ImageView imageView1 = (ImageView) findViewById(R.id.encodeImage);

                    image = android.provider.MediaStore.Images.Media.getBitmap(cr, cameraUri);
                    resizedBmp = Helpers.resizeForPreview(image);
                    Log.d("IMAGE", resizedBmp.getWidth() + " " + resizedBmp.getHeight());
                    imageView1.setImageBitmap(resizedBmp); //todo: make the image persistent between activities
                }


                catch (Exception e)
                {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.error_camera, Toast.LENGTH_SHORT);
                    toast.show();
                    e.printStackTrace();
                }

            }
            else if(requestCode == PICK_FROM_ALBUM)
            {
                Bitmap resizedBmp;
                try{
                    ImageView imageView1 = (ImageView) findViewById(R.id.encodeImage);
                    Uri selectedimg = data.getData();
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
                    resizedBmp = Helpers.resizeForPreview(image);
                    imageView1.setImageBitmap(resizedBmp);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_encode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            Bundle args = null;
            switch (position){
                case 0:
                    fragment = new EncodeFragment1();
                    args= new Bundle();
                    break;
                case 1:
                    fragment = new EncodeFragment2();
                    args= new Bundle();
                    break;

            }return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.picture);
                case 1:
                    return getString(R.string.hide_msg);
            }
            return null;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        //change
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }



        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_encode, container, false);

            return rootView;
        }
    }
}

package com.example.wngud.stegano;

import android.content.ContentResolver;
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

public class Encode extends AppCompatActivity {

    private EditText mPassField;
    private EditText mFileField;
    private RadioButton mNoneRadio;
    private RadioButton mDESRadio;
    private RadioButton mAESRadio;
    private RadioButton mBlowFishRadio;
    private EncodeControl encodeControl;
    private ImageView imageView1;
    private File tempFile;
    private Uri cameraUri;
    private Bitmap resizedBmp;

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
        encodeControl = new EncodeControl();

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

        mFileField = (EditText) findViewById(R.id.filenameField);

        mNoneRadio = (RadioButton) findViewById(R.id.noneRadio);
        mAESRadio = (RadioButton) findViewById(R.id.AESRadio);
        mDESRadio = (RadioButton) findViewById(R.id.DESRadio);
        mBlowFishRadio = (RadioButton) findViewById(R.id.BlowFishRadio);
        imageView1 = (ImageView) findViewById(R.id.encodeImage);
        //Bitmap size =  BitmapFactory.decodeResource(getResources(), R.drawable.gallery);
        //imageView1.setImageBitmap(size);


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
        encodeControl.setEncType(Encryption_type.NONE);
        mPassField.setVisibility(EditText.INVISIBLE);
    }
    public void onClickAES(View v)
    {
        mPassField = (EditText) findViewById(R.id.passwdField);
        encodeControl.setEncType(Encryption_type.AES);
        mPassField.setVisibility(EditText.VISIBLE);
    }
    public void onClickDES(View v)
    {
        mPassField = (EditText) findViewById(R.id.passwdField);
        encodeControl.setEncType(Encryption_type.DES);
        mPassField.setVisibility(EditText.VISIBLE);
    }
    public void onClickBlowFish(View v)
    {
        mPassField = (EditText) findViewById(R.id.passwdField);
        encodeControl.setEncType(Encryption_type.BLOWFISH);
        mPassField.setVisibility(EditText.VISIBLE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {

            if (requestCode == PICK_FROM_CAMERA) // 1 은 위에서 startActivityForResult(intent, 1);
            {
                ContentResolver cr = getContentResolver();
                    try {
                        imageView1 = (ImageView) findViewById(R.id.encodeImage);
                        if(imageView1 == null)
                            Log.d("null error", "camera");
                        else
                            Log.d("no null error", "camera");
                    Bitmap bm = android.provider.MediaStore.Images.Media.getBitmap(cr, cameraUri);
                    encodeControl.setPicture(bm);
                    resizedBmp = Helpers.resizeForPreview(bm);
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
                try{
                    imageView1 = (ImageView) findViewById(R.id.encodeImage);
                    if(imageView1 == null)
                        Log.d("null error", "gallery");
                    else
                        Log.d("no null error", "gallery");

                    Uri selectedimg = data.getData();
                    Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
                    encodeControl.setPicture(bm);
                    resizedBmp = Helpers.resizeForPreview(bm);
                    imageView1.setImageBitmap(resizedBmp); //todo: make the image persistent between activities
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
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "사진";
                case 1:
                    return "숨길문장";
            }
            return null;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                         Bundle savedInstanceState)
    {

        mFileField = (EditText) findViewById(R.id.filenameField);
        mPassField = (EditText) findViewById(R.id.passwdField);
        mNoneRadio = (RadioButton) findViewById(R.id.noneRadio);
        mAESRadio = (RadioButton) findViewById(R.id.AESRadio);
        mDESRadio = (RadioButton) findViewById(R.id.DESRadio);
        mBlowFishRadio = (RadioButton) findViewById(R.id.BlowFishRadio);
        imageView1 = (ImageView) findViewById(R.id.imageView);
        if(resizedBmp != null)
            imageView1.setImageBitmap(resizedBmp);
        return inflater.inflate(R.layout.fragment_encode, container, false);
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

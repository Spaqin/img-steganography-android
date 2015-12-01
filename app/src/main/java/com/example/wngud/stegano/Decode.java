package com.example.wngud.stegano;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class Decode extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private final String LOG_TAG = "Decode";
    private final int PICK_FROM_ALBUM = 0;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ImageView mDecodeGallery;
    private DecodeControl decodeControl;
    private EditText mDecodeMessage;
    private EditText mPasswdField;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);

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
        decodeControl = new DecodeControl(this);

    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_encode) {
            Intent intentEncode = new Intent(this, Encode.class);
            startActivity(intentEncode);
        } else if (id == R.id.nav_decode) {
            Intent intentDecode = new Intent(this, Decode.class);
            startActivity(intentDecode);
        } else if (id == R.id.nav_share) {
            Intent intentMMS = new Intent(this, MMS.class);
            startActivity(intentMMS);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_decode, menu);
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
    public void onClickClipboard(View v)
    {
        mDecodeMessage = (EditText) findViewById(R.id.decodeMessageField);
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Message", mDecodeMessage.getText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, R.string.message_copied, Toast.LENGTH_SHORT).show();
    }

    public void onClickSave(View v)
    {
        mDecodeMessage = (EditText) findViewById(R.id.decodeMessageField);
        try {
            File f = Helpers.saveText(mDecodeMessage.getText().toString());
            Toast.makeText(this, R.string.message_save_success + " " + f.getAbsolutePath(), Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_message_save, Toast.LENGTH_SHORT).show();
        }


    }

    public void onClickGallery(View v)
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public void onClickNoneDec(View v)
    {
        decodeControl.setEncType(Encryption_type.NONE);
        mPasswdField = (EditText) findViewById(R.id.decodePassword);
        RadioGroup secondGroup = (RadioGroup) findViewById(R.id.decodeEncryptionGroup2);
        secondGroup.clearCheck();
        mPasswdField.setVisibility(View.INVISIBLE);
    }
    public void onClickAESDec(View v)
    {
        decodeControl.setEncType(Encryption_type.AES);
        mPasswdField = (EditText) findViewById(R.id.decodePassword);
        RadioGroup secondGroup = (RadioGroup) findViewById(R.id.decodeEncryptionGroup2);
        secondGroup.clearCheck();
        mPasswdField.setVisibility(View.VISIBLE);
    }
    public void onClickDESDec(View v)
    {
        decodeControl.setEncType(Encryption_type.DES);
        mPasswdField = (EditText) findViewById(R.id.decodePassword);
        RadioGroup firstGroup = (RadioGroup) findViewById(R.id.decodeEncryptionGroup);
        firstGroup.clearCheck();
        mPasswdField.setVisibility(View.VISIBLE);
    }
    public void onClickBlowFishDec(View v)
    {
        decodeControl.setEncType(Encryption_type.BLOWFISH);
        mPasswdField = (EditText) findViewById(R.id.decodePassword);
        RadioGroup firstGroup = (RadioGroup) findViewById(R.id.decodeEncryptionGroup);
        firstGroup.clearCheck();
        mPasswdField.setVisibility(View.VISIBLE);
    }
    public void onClickDecode(View v)
    {
        mPasswdField = (EditText) findViewById(R.id.decodePassword);
        decodeControl.setKey(mPasswdField.getText().toString());
        try {
            String message = decodeControl.decode();
            if(decodeControl.isText() && message != null)
            {
                Toast.makeText(this, getString(R.string.successful_decode), Toast.LENGTH_LONG).show();
                mDecodeMessage = (EditText) findViewById(R.id.decodeMessageField);
                mDecodeMessage.setText(message);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error_decode), Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == PICK_FROM_ALBUM)
            {
                try{
                    mDecodeGallery = (ImageView) findViewById(R.id.decodePreview);

                    Uri selectedimg = data.getData();
                    Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
                    decodeControl.setPicture(bm);
                    Bitmap resizedBmp = Helpers.resizeForPreview(bm);
                    mDecodeGallery.setImageBitmap(resizedBmp);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
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
                    fragment = new DecodeFragment1();
                    args= new Bundle();
                    break;
                case 1:
                    fragment = new DecodeFragment2();
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
                    return getString(R.string.result);
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
            View rootView = inflater.inflate(R.layout.fragment_decode, container, false);
            return rootView;
        }
    }
}

package com.example.wngud.stegano;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class EncodeFragment1 extends Fragment {
    public EncodeFragment1() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_encode_fragment1, container, false);
}

    public void onViewCreated(View view, Bundle bundle)
    {
        ImageView imgView = (ImageView) getActivity().findViewById(R.id.encodeImage);
        if(Helpers.bitmap != null)
            imgView.setImageBitmap(Helpers.bitmap);
    }





}

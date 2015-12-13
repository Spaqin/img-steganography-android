package com.example.wngud.stegano;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DecodeFragment1 extends Fragment {


    public DecodeFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_decode_fragment1, container, false);
    }


    public void onViewCreated(View view, Bundle bundle)
    {
        ImageView imgView = (ImageView) getActivity().findViewById(R.id.decodePreview);
        if(Helpers.decodeBitmap != null)
            imgView.setImageBitmap(Helpers.decodeBitmap);
    }
}

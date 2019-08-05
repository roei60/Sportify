package com.example.Sportify.Activities;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Sportify.R;
import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.User;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    TextView emailTxt;
    TextView nameTxt;
    ImageView userImageView;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        emailTxt= view.findViewById(R.id.Profile_emailtxt);
        nameTxt= view.findViewById(R.id.Profile_name_txt);
        userImageView= view.findViewById(R.id.Profile_user_image_view);
        User currentUser = Dao.instance.getCurrentUser();
        emailTxt.setText(currentUser.getEmail());
        nameTxt.setText(currentUser.getName());
        Picasso.with(this.getContext()).load(currentUser.getImageUri()).fit().into(userImageView);


        return view;
    }

}

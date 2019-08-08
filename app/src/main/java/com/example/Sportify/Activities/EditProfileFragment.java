package com.example.Sportify.Activities;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Sportify.R;
import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.User;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    ImageView editProfile_image;
    TextView editProfile_name_txt;
    User currentUser;
    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentUser= Dao.instance.getCurrentUser();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        editProfile_name_txt=view.findViewById(R.id.EditProfile_name_txt);
        editProfile_image=view.findViewById(R.id.EditProfile_user_image_view);
        editProfile_name_txt.setText(currentUser.getName());
        Picasso.with(this.getContext()).load(currentUser.getImageUri()).fit().into(editProfile_image);


        return view;
    }

}

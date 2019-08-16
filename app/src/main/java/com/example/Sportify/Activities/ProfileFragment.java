package com.example.Sportify.Activities;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Sportify.R;
import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.User;
import com.example.Sportify.viewModels.PostViewModel;
import com.example.Sportify.viewModels.UserViewModel;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    TextView emailTxt;
    TextView nameTxt;
    ImageView userImageView;
    Button EditUserDataBtn;
    UserViewModel viewModel;

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
        EditUserDataBtn=view.findViewById(R.id.Profile_EditUserDataBtn);
        viewModel= ViewModelProviders.of(this).get(UserViewModel.class);
            viewModel.SetUserId(Dao.instance.getCurrentUserId(),this.getViewLifecycleOwner(),user->{
                FillUserDetails(user);
            });

        return view;
    }

    private void FillUserDetails(User user) {
        emailTxt.setText(user.getEmail());
        nameTxt.setText(user.getName());
        String imageUri = user.getImageUri();
        if(imageUri!=null)
            Picasso.with(this.getContext()).load(imageUri).fit().into(userImageView);
        EditUserDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_editProfileFragment);
            }
        });
    }

}

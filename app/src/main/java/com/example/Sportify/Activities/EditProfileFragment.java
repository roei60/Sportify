package com.example.Sportify.Activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Sportify.R;
import com.example.Sportify.dal.Model;
import com.example.Sportify.models.User;
import com.example.Sportify.utils.DateTimeUtils;
import com.example.Sportify.viewModels.UserViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    ImageView editProfile_image;
    TextView editProfile_name_txt;
    Button editProfile_editBtn;
    FloatingActionButton editProfile_choosePicBtn;
    ProgressDialog mProgressDialog;
    UserViewModel viewModel;
    User currentUser;
    private Uri mUserImageUri;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        editProfile_name_txt=view.findViewById(R.id.EditProfile_name_txt);
        editProfile_image=view.findViewById(R.id.EditProfile_user_image_view);
        editProfile_editBtn=view.findViewById(R.id.EditProfile_editBtn);
        editProfile_choosePicBtn=view.findViewById(R.id.EditProfile_add_picture_btn);
        mProgressDialog= new ProgressDialog(getActivity());

        viewModel= ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.setUserId(Model.instance.getCurrentUserId(),this.getViewLifecycleOwner(), currUser -> {
            this.currentUser=currUser ;
            editProfile_name_txt.setText(currUser .getName());
            String imageUri = currUser.getImageUri();
            if(imageUri!=null && !imageUri.isEmpty())
                Picasso.with(this.getContext()).load(imageUri).fit().into(editProfile_image);
        });
        editProfile_choosePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicture();
            }
        });
        editProfile_editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                mProgressDialog.setMessage("updating user profile...");
                mProgressDialog.show();
                final User user=new User(currentUser);
                user.setName(editProfile_name_txt.getText().toString());
                user.setLastUpdate(DateTimeUtils.getTimestampFromLong(date.getTime()));
                if (mUserImageUri != null){
                    UpdateWithImage(user);
                    }
                else
                   updateWithoutImage(user);
            }
        });

        return view;
    }
    private void UpdateWithImage(User user) {
        viewModel.uploadFile(user.getId(), mUserImageUri, new Model.UploadFileListener() {
            public void onComplete(Uri imageUri) {
                if (imageUri == null) {
                    Toast.makeText(getActivity(), "Something went wrong, pls try again later!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                final Uri cloudImageUri = imageUri;
                user.setImageUri(cloudImageUri.toString());
                updateWithoutImage(user);
            }
        });
    }
    private void updateWithoutImage(User user)
    {
        viewModel.updateUser(user, new Model.OnUpdateComleted() {
            @Override
            public void onUpdateCompleted(boolean success) {
                if (success) {
                    Toast.makeText(getActivity(), "updated user profile successfully!", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).UpdateUserData();
                } else
                    Toast.makeText(getActivity(), "Something went wrong, pls try again later!", Toast.LENGTH_SHORT).show();

                mProgressDialog.dismiss();
                ((AppCompatActivity)getContext()).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }
    private void selectProfilePicture() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setRequestedSize(1024, 1024, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .start(getActivity(),this);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        try {
            // handle result of CropImageActivity
            if (requestCode ==  CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    mUserImageUri = result.getUri();
                    Picasso.with(this.getContext()).load(mUserImageUri).fit().into(editProfile_image);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this.getActivity(), "Profile cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

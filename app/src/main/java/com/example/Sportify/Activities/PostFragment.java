package com.example.Sportify.Activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.Sportify.R;
import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    private EditText mPostEditText;
    private ImageView mPostImageView;
    private FloatingActionButton mAddPostImageBt;
    private Button mPostSendBt;

    private Uri mPostImageUri;

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        mPostEditText = view.findViewById(R.id.post_edit_text);
        mPostImageView = view.findViewById(R.id.post_image_view);
        mAddPostImageBt = view.findViewById(R.id.post_add_picture_bt);
        mPostSendBt = view.findViewById(R.id.post_send_bt);

        mAddPostImageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setRequestedSize(1024, 1024, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                        .start(getActivity(), PostFragment.this);
            }
        });

        mPostSendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if post not have image just add post
                if (mPostImageUri == null){
                    uploadPost(null);
                    return;
                }
                // upload post image and then add post with the image url
                Dao.instance.uploadFile(mPostImageUri, new Dao.UploadFileListener() {
                    @Override
                    public void onComplete(Uri imageUri) {
                        Toast.makeText(getActivity(), "Upload post image successfully!", Toast.LENGTH_SHORT).show();
                        uploadPost(imageUri);
                    }
                });
            }
        });

        return view;
    }

    private void uploadPost(Uri imageUri){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        Post post;
        if (imageUri != null)
            post = new Post(mPostEditText.getText().toString(), dateFormat.format(date), imageUri.toString());
        else
            post = new Post(mPostEditText.getText().toString(), dateFormat.format(date));
        Dao.instance.addPost(post, new Dao.AddPostListener() {
            @Override
            public void onComplete(Post post) {
                Toast.makeText(getActivity(), "Post added successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(getView()).navigate(R.id.action_postFragment_to_postsListFragment);
            }
        });
    }

    @Override
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
                    mPostImageUri = result.getUri();
                    Picasso.with(this.getContext()).load(result.getUri()).fit().into(mPostImageView);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this.getActivity(), "Post cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

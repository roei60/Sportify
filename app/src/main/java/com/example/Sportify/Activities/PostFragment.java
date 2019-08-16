package com.example.Sportify.Activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.util.Log;
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
import com.example.Sportify.utils.Consts;
import com.example.Sportify.utils.DateTimeUtils;
import com.example.Sportify.viewModels.PostViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    private String mPostId;
    private EditText mPostEditText;
    private ImageView mPostImageView;
    private FloatingActionButton mAddPostImageBt;
    private Button mPostSendBt;
    PostViewModel viewModel;
    private Uri mPostImageUri;
    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        mPostId =  CommentsFragmentArgs.fromBundle(getArguments()).getPostId();

        mPostEditText = view.findViewById(R.id.post_edit_text);
        mPostImageView = view.findViewById(R.id.post_image_view);
        mAddPostImageBt = view.findViewById(R.id.post_add_picture_bt);
        mPostSendBt = view.findViewById(R.id.post_send_bt);
        viewModel= ViewModelProviders.of(this).get(PostViewModel.class);
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

        Log.d("Tag", "mPostId = " + mPostId);

        if (mPostId != ""){
            mPostSendBt.setText("Update Post");
            viewModel.SetPostId(mPostId,this.getViewLifecycleOwner(),post -> {
                fillPostData(post);
            });
        }
        else{ // new post
            mPostSendBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Date date = new Date();
                    System.out.println(Consts.DATE_FORMAT.format(date));
                    Post post;
                    if (mPostImageUri != null)
                        post = new Post(mPostEditText.getText().toString(), Consts.DATE_FORMAT.format(date), mPostImageUri.toString());
                    else
                        post = new Post(mPostEditText.getText().toString(), Consts.DATE_FORMAT.format(date));

                    Calendar now = Calendar.getInstance();
                    post.setLastUpdate(DateTimeUtils.getTimeStamp(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH)));
                    post.setAuthorId(Dao.instance.getCurrentUserId());

                    // if post not have image just add post
                    if (mPostImageUri == null){
                        viewModel.uploadPost(post, new Dao.AddPostListener() {
                            @Override
                            public void onComplete(Post post) {
                            System.out.println("CreationDate : " + post.getCreationDate());
                            Toast.makeText(getActivity(), "Post added successfully!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(getView()).navigate(R.id.action_postFragment_to_postsListFragment);
                        }
                        });
                    }
                    else{
                        viewModel.uploadFile(mPostImageUri, new Dao.UploadFileListener() {
                            @Override
                            public void onComplete(Uri imageUri) {
                                Toast.makeText(getActivity(), "Upload post image successfully!", Toast.LENGTH_SHORT).show();
                                post.setPicture(imageUri.toString());
                                viewModel.uploadPost(post, new Dao.AddPostListener() {
                                    @Override
                                    public void onComplete(Post post) {
                                        Log.d("Tag", "Upload post successfully!");
                                        System.out.println("CreationDate : " + post.getCreationDate());
                                        Toast.makeText(getActivity(), "Post added successfully!", Toast.LENGTH_SHORT).show();
                                        Navigation.findNavController(getView()).navigate(R.id.action_postFragment_to_postsListFragment);
                                    }
                                });

                            }
                        });
                    }
                }
            });
        }

        return view;
    }

    private void fillPostData(Post post){


            Log.d("Tag", "post.text = " + post.getText());
            mPostEditText.setText(post.getText());

            if (post.getPicture() != null){
                mPostImageUri = Uri.parse(post.getPicture());
                Picasso.with(getContext()).load(post.getPicture()).fit().into(mPostImageView);
            }
            else
                mPostImageView.setImageResource(R.drawable.user_default_image);

            mPostSendBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createPost(post);
                    // if post not have image just update post
                    if (mPostImageUri == null){
                        viewModel.updatePost(post, new Dao.UpdatePostListener() {
                            @Override
                            public void onComplete(Post post) {
                                System.out.println("CreationDate : " + post.getCreationDate());
                                Toast.makeText(getActivity(), "Post updated successfully!", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(getView()).navigate(R.id.action_postFragment_to_postsListFragment);
                            }
                        });
                        return;
                    }
                    // upload post image and then add post with the image url
                    viewModel.uploadFile(mPostImageUri, new Dao.UploadFileListener() {
                        @Override
                        public void onComplete(Uri imageUri) {
                            Toast.makeText(getActivity(), "Upload post image successfully!", Toast.LENGTH_SHORT).show();
                            post.setPicture(imageUri.toString());
                            viewModel.updatePost(post, new Dao.UpdatePostListener() {
                                @Override
                                public void onComplete(Post post) {
                                    System.out.println("CreationDate : " + post.getCreationDate());
                                    Toast.makeText(getActivity(), "Post updated successfully!", Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(getView()).navigate(R.id.action_postFragment_to_postsListFragment);
                                }
                            });
                        }
                    });
                }
            });
    }

    private Post createPost(Post post){

        Date date = new Date();
        System.out.println(Consts.DATE_FORMAT.format(date));
        post.setCreationDate(Consts.DATE_FORMAT.format(date));
        post.setText(mPostEditText.getText().toString());
        if (mPostImageUri != null)
            post.setPicture(mPostImageUri.toString());
        else
            post.setPicture(null);

        return post;
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

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
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    private ProgressBar mProgressBar;
    private ProgressDialog mProgressDialog;
    private boolean isChangedPicture = false;

    private static final String UPLOAD_MASSAGE_TEXT = "Uploading post..";

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        mPostId =  PostFragmentArgs.fromBundle(getArguments()).getPostId();

        mProgressDialog = new ProgressDialog(getActivity());
        mPostEditText = view.findViewById(R.id.post_edit_text);
        mPostImageView = view.findViewById(R.id.post_image_view);
        mAddPostImageBt = view.findViewById(R.id.post_add_picture_bt);
        mPostSendBt = view.findViewById(R.id.post_send_bt);
        mProgressBar = view.findViewById(R.id.post_progressBar);
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

        if (!mPostId.isEmpty()){
            mPostSendBt.setText("Update Post");
            viewModel.setPostId(mPostId,this.getViewLifecycleOwner(), post -> {
                fillPostData(post);
            });
        }
        else{ // new post
            setLoadingUI(false);
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
                    post.setIsDeleted(false);
                    // if post not have image just add post
                    if (mPostImageUri == null){
                        mProgressDialog.setTitle(UPLOAD_MASSAGE_TEXT);
                        mProgressDialog.show();
                        addPost(post);
                    }
                    else{
                        mProgressDialog.setTitle(UPLOAD_MASSAGE_TEXT);
                        mProgressDialog.show();
                        uploadFileAndAddPost(mPostImageUri, post);
                    }
                }
            });
        }

        return view;
    }

    private void addPost(Post post){
        viewModel.uploadPost(post, new Dao.AddPostListener() {
            @Override
            public void onComplete(Post post) {
                System.out.println("CreationDate : " + post.getCreationDate());
                mProgressDialog.dismiss();
                Navigation.findNavController(getView()).navigate(R.id.action_postFragment_to_postsListFragment);
                Toast.makeText(getActivity(), "Post added successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadFileAndAddPost(Uri imageUri, Post post){
        viewModel.uploadFile(imageUri, new Dao.UploadFileListener() {
            @Override
            public void onComplete(Uri imageUri) {
                post.setPicture(imageUri.toString());
                addPost(post);
            }
        });
    }

    private void fillPostData(Post post){

        setLoadingUI(true);

        Log.d("Tag", "post.text = " + post.getText());
            mPostEditText.setText(post.getText());

            if (post.getPicture() != null){
                mPostImageUri = Uri.parse(post.getPicture());
                Picasso.with(getContext()).load(post.getPicture()).fit().into(mPostImageView);
            }
            else
                mPostImageView.setImageResource(R.drawable.default_post_mage);

            mPostSendBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createPost(post);
                    // if post not have image just update post
                    if (mPostImageUri == null){
                        mProgressDialog.setTitle(UPLOAD_MASSAGE_TEXT);
                        mProgressDialog.show();
                        updatePost(post);
                        return;
                    }
                    // upload post image and then add post with the image url
                    mProgressDialog.setTitle(UPLOAD_MASSAGE_TEXT);
                    mProgressDialog.show();
                    if (isChangedPicture){
                        uploadFileAndUpdatePost(mPostImageUri, post);
                    }
                    else{
                        updatePost(post);
                    }
                }
            });

        setLoadingUI(false);
    }

    private void uploadFileAndUpdatePost(Uri imageUri, Post post){
        viewModel.uploadFile(imageUri, new Dao.UploadFileListener() {
            @Override
            public void onComplete(Uri imageUri) {
                post.setPicture(imageUri.toString());
                updatePost(post);
            }
        });
    }

    private void updatePost(Post post){
        viewModel.updatePost(post, new Dao.UpdatePostListener() {
            @Override
            public void onComplete(Post post) {
                System.out.println("CreationDate : " + post.getCreationDate());
                mProgressDialog.dismiss();
                ((AppCompatActivity)getContext()).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Toast.makeText(getActivity(), "Post updated successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoadingUI(boolean isLoading){
        if (isLoading){
            mProgressBar.setVisibility(View.VISIBLE);
            mPostEditText.setVisibility(View.INVISIBLE);
            mPostSendBt.setVisibility(View.INVISIBLE);
            mPostImageView.setVisibility(View.INVISIBLE);
            mAddPostImageBt.hide();
        }
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mPostEditText.setVisibility(View.VISIBLE);
            mPostSendBt.setVisibility(View.VISIBLE);
            mPostImageView.setVisibility(View.VISIBLE);
            mAddPostImageBt.show();
        }

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
        post.setLastUpdate(DateTimeUtils.getTimestampFromLong(date.getTime()));
        post.setIsDeleted(false);
        return post;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            mPostImageUri = null;
            return;
        }

        try {
            // handle result of CropImageActivity
            if (requestCode ==  CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    isChangedPicture = true;
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

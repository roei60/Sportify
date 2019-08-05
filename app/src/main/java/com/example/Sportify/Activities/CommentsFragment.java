package com.example.Sportify.Activities;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.Sportify.R;
import com.example.Sportify.models.Comment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment {

    List<Comment> mComments;
    ImageButton mSendBtn;
    EditText mCommentText;

    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        mSendBtn = view.findViewById(R.id.comments_send_btn);
        mCommentText = view.findViewById(R.id.comments_edit_text);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Tag", "add comment clicked!");
            }
        });
        return view;
    }

}

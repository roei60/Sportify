package com.example.Sportify.Activities;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.Sportify.R;
import com.example.Sportify.adapters.CommentsListAdapter;
import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Comment;

import java.util.List;
import java.util.Vector;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment {

    CommentsListAdapter mAdapter;
    List<Comment> mData = new Vector<>();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    String mPostId;

    ImageButton mSendBtn;
    EditText mCommentText;

    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_comments, container, false);

        mPostId =  CommentsFragmentArgs.fromBundle(getArguments()).getPostId();

        mRecyclerView = view.findViewById(R.id.comments_list_rv);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CommentsListAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);

        mSendBtn = view.findViewById(R.id.comments_send_btn);
        mCommentText = view.findViewById(R.id.comments_edit_text);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Tag", "add comment clicked!");
            }
        });

        Dao.instance.getAllComments(mPostId, new Dao.GetAllCommentsListener() {
            @Override
            public void onComplete(List<Comment> data) {
                mData = data;
                mAdapter.mData = data;
                Log.d("Tag", "mdata count = " + mData.size());
                mAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

}

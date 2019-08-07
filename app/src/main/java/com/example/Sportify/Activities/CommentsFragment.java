package com.example.Sportify.Activities;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
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
import com.example.Sportify.models.User;
import com.example.Sportify.utils.Consts;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
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
                Date date = new Date();
                System.out.println(Consts.DATE_FORMAT.format(date));
                User user = Dao.instance.getCurrentUser();
                Comment comment = new Comment(mCommentText.getText().toString(), Consts.DATE_FORMAT.format(date), user.getId());

                Dao.instance.addComment(mPostId, comment, new Dao.AddCommentListener() {
                    @Override
                    public void onComplete(Comment comment) {
                        mData.add(comment);
                        mAdapter.mData = mData;
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        Dao.instance.getAllComments(mPostId, new Dao.GetAllCommentsListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(List<Comment> data) {
                data.sort(new Comparator<Comment>() {
                    @Override
                    public int compare(Comment o1, Comment o2) {
                        try {
                            Date date1 = Consts.DATE_FORMAT.parse(o1.getCreationDate());
                            Date date2 = Consts.DATE_FORMAT.parse(o2.getCreationDate());
                            return date1.compareTo(date2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });
                mData = data;
                mAdapter.mData = data;
                mAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

}

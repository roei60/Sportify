package com.example.Sportify.Activities;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.Sportify.R;
import com.example.Sportify.adapters.CommentsListAdapter;
import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Comment;
import com.example.Sportify.models.CommentAndUser;
import com.example.Sportify.utils.Common;
import com.example.Sportify.utils.Consts;
import com.example.Sportify.utils.DateTimeUtils;
import com.example.Sportify.viewModels.CommentViewModel;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment {

    CommentsListAdapter mAdapter;
    List<CommentAndUser> mComments = new Vector<>();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    String mPostId;

    ImageButton mSendBtn;
    EditText mCommentText;
    CommentViewModel viewModel;
    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_comments, container, false);

        viewModel= ViewModelProviders.of(this).get(CommentViewModel.class);
        mPostId =  CommentsFragmentArgs.fromBundle(getArguments()).getPostId();

        mRecyclerView = view.findViewById(R.id.comments_list_rv);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CommentsListAdapter(mComments);
        mRecyclerView.setAdapter(mAdapter);

        mSendBtn = view.findViewById(R.id.comments_send_btn);
        mCommentText = view.findViewById(R.id.comments_edit_text);


        viewModel.SetPostId(mPostId,this.getViewLifecycleOwner(),post -> {
            viewModel.observeCommentsList(getViewLifecycleOwner(), comments ->{
                if (comments != null) {
                    this.mComments.clear();
                    this.mComments.addAll(comments);
                    if (mAdapter == null) {
                        mAdapter = new CommentsListAdapter( this.mComments);
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                    addButtonsClickListeners(view);
                }
            });
            viewModel.init(getViewLifecycleOwner());
        });

        return view;
    }

    private void addButtonsClickListeners(View view) {
        mAdapter.setOnDeleteClickListener(new CommentsListAdapter.OnDeleteClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","item click: " + index);
                final CommentAndUser comment = CommentsListAdapter.mData.get(index);
                Dao.instance.deleteComment(mPostId, comment.getComment().getId(), new Dao.DeleteCommentListener() {
                    @Override
                    public void onComplete(Void avoid) {
                        Log.d("TAG","deleted comment id: " + comment.getComment().getId());
                        mComments.remove(comment);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Comment deleted successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mAdapter.setOnEditClickListener(new CommentsListAdapter.OnEditClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","item click: " + index);
                //Navigation.findNavController(view).navigate(R.id.action_cardsListFragment_to_cardDetailsFragment);
                CommentAndUser comment = CommentsListAdapter.mData.get(index);
                Log.d("TAG","comment id: " + comment.getComment().getId());

                CommentsFragmentDirections.ActionCommentsFragmentToEditCommentFragment action =
                        CommentsFragmentDirections.actionCommentsFragmentToEditCommentFragment(comment.getComment().getId(), mPostId);
                Navigation.findNavController(view).navigate(action);
            }
        });

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Tag", "add comment clicked!");
                Date date = new Date();
                System.out.println(Consts.DATE_FORMAT.format(date));
                Comment comment = new Comment(mCommentText.getText().toString(), Consts.DATE_FORMAT.format(date), Dao.instance.getCurrentUserId());
                comment.setPostId(mPostId);
                comment.setUserId(Dao.instance.getCurrentUserId());;
                comment.setLastUpdate(DateTimeUtils.getTimestampFromLong(date.getTime()));

//                viewModel.addComment(comment, new Dao.AddCommentListener() {
//                    @Override
//                    public void onComplete(Comment comment) {
//                        Common.hideKeyboard(CommentsFragment.this);
//                        mCommentText.setText("");
//                        Common.scrollToBottom(mRecyclerView);
//
//                        mComments.add(comment);
//                        mAdapter.mData = mComments;
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });
            }
        });
    }

}

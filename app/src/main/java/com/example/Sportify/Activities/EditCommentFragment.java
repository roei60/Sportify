package com.example.Sportify.Activities;


import android.app.ProgressDialog;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Sportify.R;
import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Comment;
import com.example.Sportify.models.CommentAndUser;
import com.example.Sportify.models.User;
import com.example.Sportify.utils.Common;
import com.example.Sportify.utils.Consts;
import com.example.Sportify.viewModels.CommentViewModel;
import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditCommentFragment extends Fragment {

    CommentViewModel mViewModel;
    String mPostId;
    String mCommentId;

    ImageView mUserImage;
    TextView mName;
    TextView mDate;
    TextView mText;
    ImageButton mUpdate;
    EditText mEditText;

    LinearLayout mCommentLinearLayout;
    View mCommentRow;
    ProgressBar mProgressBar;
    ProgressDialog mProgressDialog;
    public EditCommentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_comment, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mViewModel = ViewModelProviders.of(this).get(CommentViewModel.class);

        mPostId = EditCommentFragmentArgs.fromBundle(getArguments()).getPostId();
        mCommentId = EditCommentFragmentArgs.fromBundle(getArguments()).getCommentId();

        mCommentRow = view.findViewById(R.id.include);
        mProgressBar = view.findViewById(R.id.edit_comment_progressBar);
        mUserImage = view.findViewById(R.id.comment_user_img);
        mName = view.findViewById(R.id.comment_row_user_name_tv);
        mDate = view.findViewById(R.id.comment_row_date_tv);
        mText = view.findViewById(R.id.comment_row_text_tv);
        mUpdate = view.findViewById(R.id.edit_comment_btn);
        mEditText = view.findViewById(R.id.edit_comment_edit_text);
        mCommentLinearLayout = view.findViewById(R.id.edit_comment_linearLayout);

        mViewModel.setCommentId(mCommentId,this.getViewLifecycleOwner(), commentAndUser -> {
            setLoadingUI(true);
            fillCommentData(commentAndUser);
            mUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProgressDialog.setTitle("Updating comment..");
                    mProgressDialog.show();
                    Comment comment = commentAndUser.getComment();
                    comment.setText(mEditText.getText().toString());
                    Date date = new Date();
                    System.out.println(Consts.DATE_FORMAT.format(date));
                    comment.setCreationDate(Consts.DATE_FORMAT.format(date));
                    mViewModel.updateComment(comment, new Dao.UpdateCommentListener() {
                        @Override
                        public void onComplete(Comment comment) {
                            mProgressDialog.hide();
                            Common.hideKeyboard(EditCommentFragment.this);
                            Toast.makeText(getActivity(), "Comment updated successfully!", Toast.LENGTH_SHORT).show();
                            EditCommentFragmentDirections.ActionEditCommentFragmentToCommentsFragment action =
                                    EditCommentFragmentDirections.actionEditCommentFragmentToCommentsFragment(mPostId);
                            Navigation.findNavController(getView()).navigate(action);
                        }
                    });
                }
            });
            setLoadingUI(false);
        });

        return view;
    }

    private void fillCommentData(CommentAndUser commentAndUser) {
        Comment comment = commentAndUser.getComment();
        User user = commentAndUser.getUser();

        Picasso.with(getContext()).load(user.getImageUri()).fit().into(mUserImage);
        mName.setText(user.getName());
        mDate.setText(comment.getCreationDate());
        mText.setText(comment.getText());
        mEditText.setText(comment.getText());
    }

    private void setLoadingUI(boolean isLoading){
        if (isLoading){
            mProgressBar.setVisibility(View.VISIBLE);
            mCommentRow.setVisibility(View.INVISIBLE);
            mCommentLinearLayout.setVisibility(View.INVISIBLE);
        }
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mCommentRow.setVisibility(View.VISIBLE);
            mCommentLinearLayout.setVisibility(View.VISIBLE);
        }

    }

}

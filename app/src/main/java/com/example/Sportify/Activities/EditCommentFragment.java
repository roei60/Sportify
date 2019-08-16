package com.example.Sportify.Activities;


import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Sportify.R;
import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Comment;
import com.example.Sportify.utils.Consts;
import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditCommentFragment extends Fragment {

    String mPostId;
    String mCommentId;

    ImageView mUserImage;
    TextView mName;
    TextView mDate;
    TextView mText;
    ImageButton mUpdate;
    EditText mEditText;

    public EditCommentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_comment, container, false);

        mPostId = EditCommentFragmentArgs.fromBundle(getArguments()).getPostId();
        mCommentId = EditCommentFragmentArgs.fromBundle(getArguments()).getCommentId();

        mUserImage = view.findViewById(R.id.comment_user_img);
        mName = view.findViewById(R.id.comment_row_user_name_tv);
        mDate = view.findViewById(R.id.comment_row_date_tv);
        mText = view.findViewById(R.id.comment_row_text_tv);
        mUpdate = view.findViewById(R.id.edit_comment_btn);
        mEditText = view.findViewById(R.id.edit_comment_edit_text);

        Dao.instance.getComment(mPostId, mCommentId, new Dao.GetCommentListener() {
            @Override
            public void onComplete(final Comment comment) {
                Picasso.with(getContext()).load(comment.getAuthor().getImageUri()).fit().into(mUserImage);
                mName.setText(comment.getAuthor().getName());
                mDate.setText(comment.getCreationDate());
                mText.setText(comment.getText());
                mEditText.setText(comment.getText());

                mUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        comment.setText(mEditText.getText().toString());
                        Date date = new Date();
                        System.out.println(Consts.DATE_FORMAT.format(date));
                        comment.setCreationDate(Consts.DATE_FORMAT.format(date));
                        Dao.instance.updateComment(comment, new Dao.UpdateCommentListener() {
                            @Override
                            public void onComplete(Comment comment) {
                                Toast.makeText(getActivity(), "Comment updated successfully!", Toast.LENGTH_SHORT).show();
                                EditCommentFragmentDirections.ActionEditCommentFragmentToCommentsFragment action =
                                        EditCommentFragmentDirections.actionEditCommentFragmentToCommentsFragment(mPostId);
                                Navigation.findNavController(getView()).navigate(action);
                            }
                        });
                    }
                });
            }
        });

        return view;
    }

}

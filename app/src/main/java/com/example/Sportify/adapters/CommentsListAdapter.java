package com.example.Sportify.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Sportify.R;
import com.example.Sportify.dal.Model;
import com.example.Sportify.models.Comment;
import com.example.Sportify.models.CommentAndUser;
import com.example.Sportify.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentsListAdapter extends RecyclerView.Adapter<CommentsListAdapter.CommentRowViewHolder>{
    public static List<CommentAndUser> mData;
    OnItemClickListener mListener;
    OnEditClickListener mEditListener;
    OnDeleteClickListener mDeleteListener;

    public CommentsListAdapter(List<CommentAndUser> data) {
        mData = data;
    }

    public interface OnItemClickListener{
        void onClick(int index);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public interface OnEditClickListener{
        void onClick(int index);
    }
    public void setOnEditClickListener(OnEditClickListener listener){
        mEditListener = listener;
    }

    public interface OnDeleteClickListener{
        void onClick(int index);
    }
    public void setOnDeleteClickListener(OnDeleteClickListener listener){
        mDeleteListener = listener;
    }

    @NonNull
    @Override
    public CommentRowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.comment_row, viewGroup,false);
        CommentRowViewHolder viewHolder = new CommentRowViewHolder(view, mListener, mEditListener, mDeleteListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRowViewHolder postRowViewHolder, int i) {
        CommentAndUser comment = mData.get(i);
        postRowViewHolder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class CommentRowViewHolder extends RecyclerView.ViewHolder {
        ImageView mUserImage;
        TextView mName;
        TextView mDate;
        TextView mText;
        ImageButton mEdit;
        ImageButton mDelete;
        View mView;
        public CommentRowViewHolder(@NonNull final View itemView,
                                    final OnItemClickListener listener,
                                    final OnEditClickListener editListener,
                                    final OnDeleteClickListener deleteListener) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.comment_user_img);
            mName = itemView.findViewById(R.id.comment_row_user_name_tv);
            mDate = itemView.findViewById(R.id.comment_row_date_tv);
            mText = itemView.findViewById(R.id.comment_row_text_tv);
            mEdit = itemView.findViewById(R.id.comment_row_edit_bt);
            mDelete = itemView.findViewById(R.id.comment_row_delete_bt);
            mView = itemView;

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    if (deleteListener != null){
                        if (index != RecyclerView.NO_POSITION) {
                            deleteListener.onClick(index);
                        }
                    }
                }
            });

            mEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    if (editListener != null){
                        if (index != RecyclerView.NO_POSITION) {
                            editListener.onClick(index);
                        }
                    }
                }
            });
        }

        public void bind(CommentAndUser commentAndUser){
            Log.d("TAG", "bind: mText = " + mText);

            Comment comment = commentAndUser.getComment();
            User user = commentAndUser.getUser();

            if (user.getId().equals(Model.instance.getCurrentUserId())){
                // visible remove and edit buttons
                mEdit.setVisibility(View.VISIBLE);
                mDelete.setVisibility(View.VISIBLE);
            }
            else {
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
            }

            mText.setText(comment.getText());
            mDate.setText(comment.getCreationDate());
            mName.setText(user.getName());
            if (user.getImageUri() != null && !user.getImageUri().isEmpty())
                Picasso.with(itemView.getContext()).load(user.getImageUri()).fit().into(mUserImage);
            else
                mUserImage.setImageResource(R.drawable.user_default_image);
        }
    }
}
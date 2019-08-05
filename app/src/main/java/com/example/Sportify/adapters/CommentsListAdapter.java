package com.example.Sportify.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Sportify.R;
import com.example.Sportify.models.Comment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentsListAdapter extends RecyclerView.Adapter<CommentsListAdapter.CommentRowViewHolder>{
    public static List<Comment> mData;
    OnItemClickListener mListener;

    public CommentsListAdapter(List<Comment> data) {
        mData = data;
    }

    public interface OnItemClickListener{
        void onClick(int index);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public CommentRowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.comment_row, viewGroup,false);
        CommentRowViewHolder viewHolder = new CommentRowViewHolder(view, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRowViewHolder postRowViewHolder, int i) {
        Comment comment = mData.get(i);
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
        View mView;
        public CommentRowViewHolder(@NonNull final View itemView,
                                 final OnItemClickListener listener) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.comment_user_img);
            Log.d("TAG", "CommentRowViewHolder: mUserImage = " + mUserImage);
            mName = itemView.findViewById(R.id.comment_row_user_name_tv);
            Log.d("TAG", "CommentRowViewHolder: mName = " + mName);
            mDate = itemView.findViewById(R.id.comment_row_date_tv);
            Log.d("TAG", "CommentRowViewHolder: mDate = " + mDate);
            mText = itemView.findViewById(R.id.comment_row_text_tv);
            Log.d("TAG", "CommentRowViewHolder: mText = " + mText);
            mView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    if (listener != null){
                        if (index != RecyclerView.NO_POSITION) {
                            listener.onClick(index);
                        }
                    }
                }
            });
        }

        public void bind(Comment comment){
            Log.d("TAG", "bind: mText = " + mText);
            mText.setText(comment.getText());
            mDate.setText(comment.getCreationDate());
            mName.setText(comment.getAuthor().getName());
            if (comment.getAuthor().getImageUri() != null)
                Picasso.with(itemView.getContext()).load(comment.getAuthor().getImageUri()).fit().into(mUserImage);
            else
                mUserImage.setImageResource(R.drawable.user_default_image);
        }
    }
}
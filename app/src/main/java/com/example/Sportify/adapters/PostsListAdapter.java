package com.example.Sportify.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Sportify.R;
import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostsListAdapter extends RecyclerView.Adapter<PostsListAdapter.PostRowViewHolder>{
    public static List<Post> mData;
    OnItemClickListener mListener;
    OnEditClickListener mEditListener;
    OnDeleteClickListener mDeleteListener;

    public PostsListAdapter(List<Post> data) {
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
    public PostRowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.post_row, viewGroup,false);
        PostRowViewHolder viewHolder = new PostRowViewHolder(view, mListener, mEditListener, mDeleteListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostRowViewHolder postRowViewHolder, int i) {
        Post post = mData.get(i);
        postRowViewHolder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class PostRowViewHolder extends RecyclerView.ViewHolder {
        ImageView mUserImage;
        TextView mName;
        TextView mDate;
        TextView mText;
        ImageView mPostImage;
        CheckBox mLikeCb;
        ImageButton mComment;
        ImageButton mEdit;
        ImageButton mDelete;
        View mView;

        public PostRowViewHolder(@NonNull final View itemView,
                                 final OnItemClickListener listener,
                                 final OnEditClickListener editListener,
                                 final OnDeleteClickListener deleteListener) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.post_user_img);
            mName = itemView.findViewById(R.id.post_row_user_name_tv);
            mDate = itemView.findViewById(R.id.post_row_date_tv);
            mText = itemView.findViewById(R.id.post_row_text_tv);
            mPostImage = itemView.findViewById(R.id.post_row_image_view);
            mLikeCb = itemView.findViewById(R.id.post_row_like_cb);
            mComment = itemView.findViewById(R.id.post_row_comment_bt);
            mEdit = itemView.findViewById(R.id.post_row_edit_bt);
            mDelete = itemView.findViewById(R.id.post_row_delete_bt);
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

            mComment.setOnClickListener(new View.OnClickListener() {
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

        public void bind(Post post){
            if (post.getAuthor().getId().equals(Dao.instance.getCurrentUser().getId())){
                // visible remove and edit buttons
                mEdit.setVisibility(View.VISIBLE);
                mDelete.setVisibility(View.VISIBLE);
            }
            else {
                mEdit.setVisibility(View.GONE);
                mDelete.setVisibility(View.GONE);
            }
            mText.setText(post.getText());
            mDate.setText(post.getCreationDate());
            mName.setText(post.getAuthor().getName());
            if (post.getAuthor().getImageUri() != null)
                Picasso.with(itemView.getContext()).load(post.getAuthor().getImageUri()).fit().into(mUserImage);
            else
                mUserImage.setImageResource(R.drawable.user_default_image);

            if (post.getPicture() != null) {
                Picasso.with(itemView.getContext()).load(post.getPicture()).fit().into(mPostImage);
                mPostImage.setVisibility(View.VISIBLE);
            }
            else{
                mPostImage.setVisibility(View.GONE);
            }
        }
    }
}
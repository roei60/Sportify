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
import com.example.Sportify.models.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostsListAdapter extends RecyclerView.Adapter<PostsListAdapter.PostRowViewHolder>{
    public static List<Post> mData;
    OnItemClickListener mListener;

    public PostsListAdapter(List<Post> data) {
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
    public PostRowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.post_row, viewGroup,false);
        PostRowViewHolder viewHolder = new PostRowViewHolder(view, mListener);
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
        View mView;
        public PostRowViewHolder(@NonNull final View itemView,
                                 final OnItemClickListener listener) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.post_user_img);
            mName = itemView.findViewById(R.id.post_row_user_name_tv);
            mDate = itemView.findViewById(R.id.post_row_date_tv);
            mText = itemView.findViewById(R.id.post_row_text_tv);
            mPostImage = itemView.findViewById(R.id.post_row_image_view);
            mLikeCb = itemView.findViewById(R.id.post_row_like_cb);
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

        public void bind(Post post){
            mText.setText(post.getText());
            mDate.setText(post.getCreationDate());
            mName.setText(post.getAuthor().getName());
            if (post.getAuthor().getImageUri() != null)
                Picasso.with(itemView.getContext()).load(post.getAuthor().getImageUri()).fit().into(mUserImage);
            else
                mUserImage.setImageResource(R.drawable.user_default_image);

            if (post.getPicture() != null) {
                Log.d("Tag", "pctureUri = " + post.getPicture());
                Picasso.with(itemView.getContext()).load(post.getPicture()).fit().into(mPostImage);
                mPostImage.setVisibility(View.VISIBLE);
            }
            else{
                mPostImage.setVisibility(View.GONE);
            }
        }
    }
}
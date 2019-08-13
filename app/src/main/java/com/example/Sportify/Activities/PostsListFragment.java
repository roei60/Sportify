package com.example.Sportify.Activities;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.Sportify.R;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Post;
import com.example.Sportify.utils.Consts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.Sportify.adapters.PostsListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import static android.app.Activity.RESULT_OK;

public class PostsListFragment extends Fragment {
    PostsListAdapter mAdapter;
    List<Post> mData = new Vector<>();

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    FirebaseUser mUserDetails;
    private FloatingActionButton mAddCardBtn;
    private FloatingActionButton mShareCardsBtn;

    private ProgressBar mProgressBar;
    private SearchView searchView;
    public PostsListFragment() {
        // Required empty public constructor
    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_posts_list, container, false);
        mRecyclerView = view.findViewById(R.id.cards_list_rv);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PostsListAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);

        // TODO: Navigate to cardDetails fragment
        mAdapter.setOnItemClickListener(new PostsListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","item click: " + index);
                //Navigation.findNavController(view).navigate(R.id.action_cardsListFragment_to_cardDetailsFragment);
                Post post = PostsListAdapter.mData.get(index);
                Log.d("TAG","post id: " + post.getId());

                PostsListFragmentDirections.ActionPostsListFragmentToCommentsFragment action =
                        PostsListFragmentDirections.actionPostsListFragmentToCommentsFragment(post.getId());
                Navigation.findNavController(view).navigate(action);
            }
        });

        mAdapter.setOnEditClickListener(new PostsListAdapter.OnEditClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","item click: " + index);
                //Navigation.findNavController(view).navigate(R.id.action_cardsListFragment_to_cardDetailsFragment);
                Post post = PostsListAdapter.mData.get(index);
                Log.d("TAG","post id: " + post.getId());

                PostsListFragmentDirections.ActionPostsListFragmentToPostFragment action =
                        PostsListFragmentDirections.actionPostsListFragmentToPostFragment(post.getId());
                Navigation.findNavController(view).navigate(action);
            }
        });

        mAdapter.setOnDeleteClickListener(new PostsListAdapter.OnDeleteClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","item click: " + index);
                //Navigation.findNavController(view).navigate(R.id.action_cardsListFragment_to_cardDetailsFragment);
                final Post post = PostsListAdapter.mData.get(index);
                Dao.instance.deletePost(Dao.instance.getCurrentUser().getId(), post.getId(), new Dao.DeletePostListener() {
                    @Override
                    public void onComplete(Void avoid) {
                        Log.d("TAG","deleted post id: " + post.getId());
                        mData.remove(post);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Post deleted successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mAddCardBtn = view.findViewById(R.id.cards_list_add_bt);
//        mAddCardBtn.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        onSelectImageClick();
//                    }
//                }
//        );

        mShareCardsBtn = view.findViewById(R.id.cards_list_share_bt);
        //mShareCardsBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_cardsListFragment_to_usersListFragment));

        mProgressBar = view.findViewById(R.id.cards_list_pb);
        mProgressBar.setVisibility(View.INVISIBLE);
        setHasOptionsMenu(true);

        /*Dao.instance.getAllPosts(new Dao.GetAllPostsListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(List<Post> data) {
                data.sort(new Comparator<Post>() {
                    @Override
                    public int compare(Post o1, Post o2) {
                        try {
                            Date date1 = Consts.DATE_FORMAT.parse(o1.getCreationDate());
                            Date date2 = Consts.DATE_FORMAT.parse(o2.getCreationDate());
                            return date2.compareTo(date1);
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
        });*/

        return view;
    }
}

package com.example.Sportify.Activities;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.Sportify.Adapters.PostsListAdapter;
import com.example.Sportify.R;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.Post;
import com.example.Sportify.models.PostAndUser;
import com.example.Sportify.models.User;
import com.example.Sportify.utils.Consts;
import com.example.Sportify.viewModels.PostsListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
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

import com.example.Sportify.Adapters.PostsListAdapter;
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
import java.util.Objects;
import java.util.Vector;

import static android.app.Activity.RESULT_OK;

public class PostsListFragment extends Fragment {
    PostsListAdapter mAdapter;
    List<PostAndUser> mPosts = new Vector<>();

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    FirebaseUser mUserDetails;
    private FloatingActionButton mAddCardBtn;
    private FloatingActionButton mShareCardsBtn;

    private ProgressBar mProgressBar;
    private SearchView searchView;
    private PostsListViewModel viewModel;

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

        mProgressBar = view.findViewById(R.id.cards_list_pb);
        mProgressBar.setVisibility(View.INVISIBLE);
        setHasOptionsMenu(true);

        viewModel = ViewModelProviders.of(this).get(PostsListViewModel.class);

        viewModel.observePostsList(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                this.mPosts.clear();
                this.mPosts.addAll(posts);
                if (mAdapter == null) {
                    mAdapter = new PostsListAdapter(this.mPosts);
                    mRecyclerView.setAdapter(mAdapter);
                    addButtonsClickListeners(view);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });


        viewModel.init(getViewLifecycleOwner());
        return view;
    }

    private void addButtonsClickListeners(View view) {
        // TODO: Navigate to cardDetails fragment
        mAdapter.setOnItemClickListener(new PostsListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","item click: " + index);
                //Navigation.findNavController(view).navigate(R.id.action_cardsListFragment_to_cardDetailsFragment);
                PostAndUser post = PostsListAdapter.mData.get(index);
                Log.d("TAG","post id: " + post.getPost().getId());

                PostsListFragmentDirections.ActionPostsListFragmentToCommentsFragment action =
                        PostsListFragmentDirections.actionPostsListFragmentToCommentsFragment(post.getPost().getId());
                Navigation.findNavController(view).navigate(action);
            }
        });

        mAdapter.setOnEditClickListener(new PostsListAdapter.OnEditClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","item click: " + index);
                //Navigation.findNavController(view).navigate(R.id.action_cardsListFragment_to_cardDetailsFragment);
                PostAndUser post = PostsListAdapter.mData.get(index);
                Log.d("TAG","post id: " + post.getPost().getId());

                PostsListFragmentDirections.ActionPostsListFragmentToPostFragment action =
                        PostsListFragmentDirections.actionPostsListFragmentToPostFragment(post.getPost().getId());
                Navigation.findNavController(view).navigate(action);
            }
        });

        mAdapter.setOnDeleteClickListener(new PostsListAdapter.OnDeleteClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","item click: " + index);
                //Navigation.findNavController(view).navigate(R.id.action_cardsListFragment_to_cardDetailsFragment);
                final PostAndUser post = PostsListAdapter.mData.get(index);
                Dao.instance.deletePost(Dao.instance.getCurrentUser().getId(), post.getPost().getId(), new Dao.DeletePostListener() {
                    @Override
                    public void onComplete(Void avoid) {
                        Log.d("TAG","deleted post id: " + post.getPost().getId());
                        mPosts.remove(post);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Post deleted successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

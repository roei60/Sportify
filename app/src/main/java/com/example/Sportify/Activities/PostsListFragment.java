package com.example.Sportify.Activities;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.Sportify.Adapters.PostsListAdapter;
import com.example.Sportify.R;

import com.example.Sportify.dal.Model;
import com.example.Sportify.models.PostAndUser;
import com.example.Sportify.utils.DateTimeUtils;
import com.example.Sportify.viewModels.PostsListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.List;
import java.util.Vector;

public class PostsListFragment extends Fragment {
    PostsListAdapter mAdapter;
    List<PostAndUser> mPosts = new Vector<>();

    PostsListViewModel mViewModel;
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

        mViewModel = ViewModelProviders.of(this).get(PostsListViewModel.class);

        mRecyclerView = view.findViewById(R.id.cards_list_rv);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mProgressBar = view.findViewById(R.id.cards_list_pb);
        mProgressBar.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
       super.onViewCreated(view,savedInstanceState);

       mViewModel.observePostsList(getViewLifecycleOwner(), posts -> {
           Log.d("Tag", "posts size in fragment = " + posts.size());
            if (posts != null) {
                this.mPosts.clear();
                this.mPosts.addAll(posts);
                mAdapter = new PostsListAdapter( this.mPosts);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                addButtonsClickListeners(view);
            }
        });

        mViewModel.init(getViewLifecycleOwner());

    }


    private void removeButtonsListeners(){
        Log.d("TAG","before remove buttons listeners");
        mAdapter.setOnEditClickListener(null);
        mAdapter.setOnItemClickListener(null);
        mAdapter.setOnDeleteClickListener(null);
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

                removeButtonsListeners();
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

                removeButtonsListeners();
            }
        });

        mAdapter.setOnDeleteClickListener(new PostsListAdapter.OnDeleteClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","item click: " + index);
                //Navigation.findNavController(view).navigate(R.id.action_cardsListFragment_to_cardDetailsFragment);
                final PostAndUser post = PostsListAdapter.mData.get(index);
                post.getPost().setIsDeleted(true);
                post.getPost().setLastUpdate(DateTimeUtils.getTimestampFromLong(new Date().getTime()));
                mViewModel.deltePost(post, new Model.DeletePostListener() {
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

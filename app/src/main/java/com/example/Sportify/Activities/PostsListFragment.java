package com.example.Sportify.Activities;


import android.os.Bundle;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        mUserDetails=FirebaseAuth.getInstance().getCurrentUser();

        // TODO: Navigate to cardDetails fragment
        mAdapter.setOnItemClickListener(new PostsListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","item click: " + index);
                //Navigation.findNavController(view).navigate(R.id.action_cardsListFragment_to_cardDetailsFragment);
                Post post = PostsListAdapter.mData.get(index);
//                CardsListFragmentDirections.ActionCardsListFragmentToCardDetailsFragment action =
//                        CardsListFragmentDirections.actionCardsListFragmentToCardDetailsFragment(
//                                card.getPersonName(),
//                                card.getPhoneNumber(),
//                                card.getCompany(),
//                                card.getAddress(),
//                                card.getEmail(),
//                                card.getWebsite(),
//                                Uri.parse(card.getImageUri())
//                        );
//                Navigation.findNavController(view).navigate(action);
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

        Dao.instance.getAllPosts(new Dao.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> data) {
                mData = data;
                mAdapter.mData = data;
                mAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}

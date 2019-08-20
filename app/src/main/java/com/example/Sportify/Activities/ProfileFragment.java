package com.example.Sportify.Activities;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Sportify.R;
import com.example.Sportify.dal.Model;
import com.example.Sportify.models.PostAndUser;
import com.example.Sportify.models.User;
import com.example.Sportify.viewModels.PostsListViewModel;
import com.example.Sportify.viewModels.UserViewModel;
import com.squareup.picasso.Picasso;
import com.example.Sportify.Adapters.PostsListAdapter;

import java.util.List;
import java.util.Vector;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    TextView emailTxt;
    TextView nameTxt;
    ImageView userImageView;
    Button EditUserDataBtn;
    UserViewModel mViewModel;
    PostsListViewModel mPostsListViewModel;

    PostsListAdapter mAdapter;
    List<PostAndUser> mPosts = new Vector<>();

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        emailTxt= view.findViewById(R.id.Profile_emailtxt);
        nameTxt= view.findViewById(R.id.Profile_name_txt);
        userImageView= view.findViewById(R.id.Profile_user_image_view);
        EditUserDataBtn=view.findViewById(R.id.Profile_EditUserDataBtn);
        mRecyclerView = view.findViewById(R.id.profile_posts_list_rv);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);


        mPostsListViewModel = ViewModelProviders.of(this).get(PostsListViewModel.class);
        mViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mViewModel.setUserId(Model.instance.getCurrentUserId(),this.getViewLifecycleOwner(), user -> {
            FillUserDetails(user);
        });

        mViewModel.observeUserPostsList(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                this.mPosts.clear();
                this.mPosts.addAll(posts);
                if (mAdapter == null) {
                    mAdapter = new PostsListAdapter( this.mPosts);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                addButtonsClickListeners(view);
            }
        });

        mViewModel.init(getViewLifecycleOwner());

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

                ProfileFragmentDirections.ActionProfileFragmentToCommentsFragment action =
                        ProfileFragmentDirections.actionProfileFragmentToCommentsFragment(post.getPost().getId());
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

                ProfileFragmentDirections.ActionProfileFragmentToPostFragment action =
                        ProfileFragmentDirections.actionProfileFragmentToPostFragment(post.getPost().getId());
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
                mPostsListViewModel.deltePost(post, new Model.DeletePostListener() {
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

    private void FillUserDetails(User user) {
        emailTxt.setText(user.getEmail());
        nameTxt.setText(user.getName());
        String imageUri = user.getImageUri();
        if(imageUri!=null&& !imageUri.isEmpty())
            Picasso.with(this.getContext()).load(imageUri).fit().into(userImageView);
        EditUserDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_editProfileFragment);
                removeButtonsListeners();
            }
        });
    }

    private void removeButtonsListeners() {
        Log.d("TAG","before remove buttons listeners");
        mAdapter.setOnEditClickListener(null);
        mAdapter.setOnItemClickListener(null);
        mAdapter.setOnDeleteClickListener(null);
    }

}

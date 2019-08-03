package com.example.Sportify.dal;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.Sportify.models.Post;
import com.example.Sportify.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.annotation.Nullable;

public class FirebaseDao {

    FirebaseFirestore db;
    FirebaseAuth auth;

    public FirebaseDao() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false).build();
        db.setFirestoreSettings(settings);
    }

    public void getAllPosts(final Dao.GetAllPostsListener listener) {
        db.collection("Users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        final List<Post> data = new Vector<>();
                        if (e != null) {
                            listener.onComplete(data);
                            return;
                        }
                        for (QueryDocumentSnapshot userDoc : queryDocumentSnapshots) {
                            final User user = userDoc.toObject(User.class);
                            userDoc.getReference().collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    for(QueryDocumentSnapshot postDoc: queryDocumentSnapshots)
                                    {
                                        Post post = postDoc.toObject(Post.class);
                                        post.setId(postDoc.getId());
                                        post.setAuthor(user);
                                        data.add(post);
                                    }
                                    listener.onComplete(data);
                                }
                            });
                        }
                    }
                });
    }

    public void addPost(Post post) {
        db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Posts").document(post.getId()).set(post);
    }

    interface GetPostsListener {
        void onComplete(Post post);
    }

    public void getPost(String id, final GetPostsListener listener) {
        db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Posts").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            Post post = snapshot.toObject(Post.class);
                            listener.onComplete(post);
                            return;
                        }
                        listener.onComplete(null);
                    }
                });
    }
}

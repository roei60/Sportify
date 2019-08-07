package com.example.Sportify.dal;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.Sportify.models.Comment;
import com.example.Sportify.models.Post;
import com.example.Sportify.models.User;
import com.example.Sportify.utils.FileUtils;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.annotation.Nullable;

public class FirebaseDao {
    User _currentUser;
    FirebaseFirestore db;
    FirebaseAuth auth;
    StorageReference storage;

    public FirebaseDao() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference("Uploads/" + auth.getCurrentUser().getUid() + "/ProfilePics");

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

    public void addPost(final Post post, final Dao.AddPostListener listener) {
        db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Posts").document(post.getId()).set(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        listener.onComplete(post);
                    }
                });
    }

    interface GetPostsListener {
        void onComplete(Post post);
    }

    public void getPost(final String id, final GetPostsListener listener) {
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> documents = result.getDocuments();
                    for (final DocumentSnapshot userDoc: documents) {
                        final User user = userDoc.toObject(User.class);
                        user.setId(userDoc.getId());
                        userDoc.getReference().collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot postsSnapshots = task.getResult();
                                    List<DocumentSnapshot> postDocuments = postsSnapshots.getDocuments();
                                    final List<Comment> comments = new ArrayList<>();
                                    for (final DocumentSnapshot postDoc: postDocuments) {
                                        if (postDoc.getId().equals(id)){
                                            postDoc.getReference().collection("Comments").get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            List<DocumentSnapshot> commentDocs = queryDocumentSnapshots.getDocuments();

                                                            for (DocumentSnapshot commentDoc: commentDocs ) {
                                                                final Comment comment = commentDoc.toObject(Comment.class);
                                                                comment.setId(commentDoc.getId());
                                                                comment.setAuthor(user);
                                                                comments.add(comment);
                                                                Log.d("Tag", "adding comment");
                                                            }
                                                            Post post = postDoc.toObject(Post.class);
                                                            post.setId(postDoc.getId());
                                                            post.setComments(comments);
                                                        }
                                                    });
                                        }
                                    }
                                }
                                else
                                    listener.onComplete(null);
                            }
                        });
                    }
                }
                else
                    listener.onComplete(null);
            }
        });
    }


    public void uploadFile(Uri imageUri, final Dao.UploadFileListener listener){

        final StorageReference fileRef = storage.child(System.currentTimeMillis() + "." + FileUtils.getFileExtension(imageUri));
        UploadTask uploadTask = fileRef.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    listener.onComplete(downloadUri);
                } else {
                    listener.onComplete(null);
                }
            }
        });
    }

    public void  getUser(final String id, final Dao.GetUserDetailsListener listener){
        db.collection("Users").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            final User user= snapshot.toObject(User.class);
                            user.setId(id);
                            snapshot.getReference().collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    for(QueryDocumentSnapshot postDoc: queryDocumentSnapshots)
                                    {
                                        Post post = postDoc.toObject(Post.class);
                                        post.setId(postDoc.getId());
                                        post.setAuthor(user);
                                        user.getPosts().add(post);
                                    }
                                    listener.onComplete(user);

                                }
                            });

                            return;
                        }
                        listener.onComplete(null);
                    }
                });
    }

    public void getAllUsers(final Dao.GetAllUsersListener listener){
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> documents = result.getDocuments();
                    List<User> users = new ArrayList<>();
                    for (DocumentSnapshot userDoc : documents) {
                        User user = userDoc.toObject(User.class);
                        user.setId(userDoc.getId());
                        users.add(user);
                    }
                    listener.onComplete(users);
                }
                else
                    listener.onComplete(null);
            }
        });
    }

    public void getAllComments(final String postId, final Dao.GetAllCommentsListener listener){
        getAllUsers(new Dao.GetAllUsersListener() {
            @Override
            public void onComplete(final List<User> users) {
                db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            List<DocumentSnapshot> documents = result.getDocuments();
                            for (DocumentSnapshot userDoc: documents) {
                                final User user= userDoc.toObject(User.class);
                                user.setId(userDoc.getId());
                                userDoc.getReference().collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot postsSnapshots = task.getResult();
                                            List<DocumentSnapshot> postDocuments = postsSnapshots.getDocuments();
                                            final List<Comment> comments = new ArrayList<>();
                                            for (DocumentSnapshot postDoc: postDocuments) {
                                                if (postDoc.getId().equals(postId)){
                                                    postDoc.getReference().collection("Comments").get()
                                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                    List<DocumentSnapshot> commentDocs = queryDocumentSnapshots.getDocuments();

                                                                    for (DocumentSnapshot commentDoc: commentDocs ) {
                                                                        final Comment comment = commentDoc.toObject(Comment.class);
                                                                        comment.setId(commentDoc.getId());
                                                                        for (User user: users) {
                                                                            if (user.getId().equals(commentDoc.get("userId"))){
                                                                                comment.setAuthor(user);
                                                                                comments.add(comment);
                                                                                Log.d("Tag", "adding comment");
                                                                            }
                                                                        }
                                                                    }
                                                                    listener.onComplete(comments);
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                        else
                                            listener.onComplete(null);
                                    }
                                });
                            }
                        }
                        else
                            listener.onComplete(null);
                    }
                });
            }
        });
    }

    public void addComment(final String postId, final Comment comment, final Dao.AddCommentListener listener) {

        getAllUsers(new Dao.GetAllUsersListener() {
            @Override
            public void onComplete(final List<User> users) {
                db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            List<DocumentSnapshot> documents = result.getDocuments();
                            for (DocumentSnapshot userDoc: documents) {
                                final User user= userDoc.toObject(User.class);
                                user.setId(userDoc.getId());
                                userDoc.getReference().collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot postsSnapshots = task.getResult();
                                            List<DocumentSnapshot> postDocuments = postsSnapshots.getDocuments();
                                            final List<Comment> comments = new ArrayList<>();
                                            for (DocumentSnapshot postDoc: postDocuments) {
                                                if (postDoc.getId().equals(postId)){
                                                    postDoc.getReference().collection("Comments").document(comment.getId()).set(comment)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    for (User user: users) {
                                                                        if (user.getId().equals(comment.getUserId())){
                                                                            comment.setAuthor(user);
                                                                            Log.d("Tag", "adding comment");
                                                                        }
                                                                    }
                                                                    listener.onComplete(comment);
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                        else
                                            listener.onComplete(null);
                                    }
                                });
                            }
                        }
                        else
                            listener.onComplete(null);
                    }
                });
            }
        });

    }
}

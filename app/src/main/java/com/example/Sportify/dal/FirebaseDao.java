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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                            user.setId(userDoc.getId());
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

    public void getPost(final String id, final Dao.GetPostListener listener) {
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
                                                            post.setAuthor(user);
                                                            post.setComments(comments);
                                                            listener.onComplete(post);
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

    public void deletePost(final String userId, final String postId, final Dao.DeletePostListener listener) {
        db.collection("Users").document(userId).collection("Posts").document(postId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onComplete(aVoid);
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
    public void signIn(String email, String password, final Dao.OnUpdateComleted listener){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                listener.onUpdateCompleted(task.isSuccessful());
            }
        });
    }

    public void registerUser(final User user, String password, final Uri userImageUri, final Dao.OnUpdateComleted listener)
    {
        auth.createUserWithEmailAndPassword(user.getEmail(),password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // if user image chosen - upload to firebase sorage
                    final String uid = auth.getCurrentUser().getUid();
                    if (userImageUri != null)
                        uploadProfileImageFile(uid, userImageUri, new Dao.UploadFileListener() {
                            @Override
                            public void onComplete(Uri imageUri) {
                                user.setImageUri(imageUri.toString());
                                saveUser(user,listener);
                            }
                        });
                    else
                        saveUser(user,listener);
                } else {
                    listener.onUpdateCompleted(false);
                }

            }
        });
    }
    private void saveUser(User user, final Dao.OnUpdateComleted listener) {
        String uid = auth.getCurrentUser().getUid();
        user.setId(uid);
        final CollectionReference UserRef = db.collection("Users");
        UserRef.document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onUpdateCompleted(task.isSuccessful());
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

    public void UpdateUserProfile(final User user, final Dao.OnUpdateComleted listener) {
        final CollectionReference UserRef = db.collection("Users");
        UserRef.document(user.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    Map<Object, String> map = new HashMap<>();
                    map.put("name", user.getName());
                    map.put("imageUri", user.getImageUri());
                    UserRef.document(result.getId()).set(map, SetOptions.merge());
                    Dao.instance.setCurrentUser(user);
                    listener.onUpdateCompleted(true);
                }
                else
                    listener.onUpdateCompleted(false);
            }
        });
    }


    public void uploadProfileImageFile(String userId, Uri imageProfile, final Dao.UploadFileListener listener)
    {
        final StorageReference fileRef = FirebaseStorage.getInstance().getReference("Uploads/" + userId + "/ProfilePics")
                .child(System.currentTimeMillis() + "." + FileUtils.getFileExtension(imageProfile));
        UploadTask uploadTask = fileRef.putFile(imageProfile);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                    //Toast.makeText(getActivity(), "Something got wrong.. pls try again", Toast.LENGTH_LONG).show();
                }
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

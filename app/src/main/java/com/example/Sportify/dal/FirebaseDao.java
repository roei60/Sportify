package com.example.Sportify.dal;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.Sportify.models.Comment;
import com.example.Sportify.models.Post;
import com.example.Sportify.models.User;
import com.example.Sportify.utils.Consts;
import com.example.Sportify.utils.DateTimeUtils;
import com.example.Sportify.utils.FileUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import javax.annotation.Nullable;

public class FirebaseDao {
    User _currentUser;
    FirebaseFirestore db;
    FirebaseAuth auth;
    StorageReference storage;
    CollectionReference userRef;
    CollectionReference postRef;
    CollectionReference commentRef;
    private ListenerRegistration listenerRegistrationPost;
    private ListenerRegistration listenerRegistrationComment;
    private ListenerRegistration listenerRegistrationUsers;

    public FirebaseDao() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false).build();
        db.setFirestoreSettings(settings);

        userRef=db.collection("Users");
        postRef=db.collection("Posts");
        commentRef=db.collection("Comments");
    }



    public void getAllComments(long updateFrom,final IFirebaseListener listener) {
        if(listenerRegistrationComment!=null)
            listenerRegistrationComment.remove();
        Timestamp timeStamp;
        if(updateFrom==0) {
            timeStamp = DateTimeUtils.getTimeStamp(2019, 1, 1);
            Log.d("Tag", "getAllPosts if: timeStamp = " + timeStamp);
        }
        else{
            timeStamp=DateTimeUtils.getTimestampFromLong(updateFrom);
            Log.d("Tag", "getAllPosts else: timeStamp = " + timeStamp);
        }

        getAllComments(timeStamp,listener);
    }
    private void getAllComments(Timestamp from, IFirebaseListener listener)
    {
        listenerRegistrationComment = commentRef.whereGreaterThan("lastUpdate", from).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                List<Comment> comments= new ArrayList<>();
                snapshot.getDocumentChanges().get(0).getDocument().toObject(Comment.class);
                for (DocumentChange docChange : snapshot.getDocumentChanges()) {
                    comments.add(docChange.getDocument().toObject(Comment.class));
                }
                Log.d("Tag", "comments size in observer = " + comments.size());
                comments= snapshot.toObjects(Comment.class);
                listener.updatedCommentsForPosts(comments);
            }
        });
    }


    public void getAllPosts(long updateFrom,final IFirebaseListener listener) {
        if(listenerRegistrationPost!=null)
            listenerRegistrationPost.remove();
        Timestamp timeStamp;
        if(updateFrom==0) {
            timeStamp = DateTimeUtils.getTimeStamp(2019, 1, 1);
            Log.d("Tag", "getAllPosts if: timeStamp = " + timeStamp);
        }
        else{
            timeStamp=DateTimeUtils.getTimestampFromLong(updateFrom);
            Log.d("Tag", "getAllPosts else: timeStamp = " + timeStamp);
        }

        getAllPosts(timeStamp,listener);
    }
    private  void getAllPosts(Timestamp from, IFirebaseListener listener)
    {
        listenerRegistrationPost = postRef.whereGreaterThan("lastUpdate", from).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                List<Post> posts= new ArrayList<>();
                snapshot.getDocumentChanges().get(0).getDocument().toObject(Post.class);
                for (DocumentChange docChange : snapshot.getDocumentChanges()) {
                    posts.add(docChange.getDocument().toObject(Post.class));
                }
                Log.d("Tag", "posts size in observer = " + posts.size());
                posts= snapshot.toObjects(Post.class);
                listener.updatePosts(posts);
            }
        });
    }

    public void getAllUsers(long updateFrom,final IFirebaseListener listener) {
        if(listenerRegistrationUsers!=null)
            listenerRegistrationUsers.remove();
        Timestamp timeStamp;
        if(updateFrom==0)
            timeStamp = DateTimeUtils.getTimeStamp(2019, 1, 1);
        else
            timeStamp=DateTimeUtils.getTimestampFromLong(updateFrom);
        getAllUsers(timeStamp,listener);
    }
    private  void getAllUsers(Timestamp from, IFirebaseListener listener)
    {
        listenerRegistrationUsers = userRef.whereGreaterThan("lastUpdate", from).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                List<User> users= new ArrayList<>();
                snapshot.getDocumentChanges().get(0).getDocument().toObject(User.class);
                for (DocumentChange docChange : snapshot.getDocumentChanges()) {
                    users.add(docChange.getDocument().toObject(User.class));
                }
                users= snapshot.toObjects(User.class);
                listener.updateUsers(users);
            }
        });
    }

    public void updatePost(final Post post, final Dao.UpdatePostListener listener){
        postRef.document(post.getId()).set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onComplete(post);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Tag", "onFailure: updatePost " + post.getId() + " failed");
                listener.onComplete(null);
            }
        });
    }

    public void addPost(final Post post, final Dao.AddPostListener listener) {
        String id = postRef.document().getId();
        post.setId(id);
        postRef.document(id).set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onComplete(post);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Tag", "onFailure: " + e.getMessage());
                listener.onComplete(null);
            }
        });
    }

    public void deletePost(String postId, final Dao.DeletePostListener listener) {
        postRef.document(postId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onComplete(aVoid);
                    }
                });
    }

    public void uploadFile(Uri imageUri, final Dao.UploadFileListener listener){
        storage = FirebaseStorage.getInstance().getReference("Uploads/" + auth.getCurrentUser().getUid() + "/ProfilePics");

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
        java.util.Date date = new Date();
        user.setLastUpdate(DateTimeUtils.getTimestampFromLong(date.getTime()));
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
        userRef.document(user.getId()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onUpdateCompleted(true);
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
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

    public void updateComment(final Comment comment, final Dao.UpdateCommentListener listener){
        commentRef.document(comment.getId()).set(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onComplete(comment);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Tag", "onFailure: updateComment " + comment.getId() + " failed");
                listener.onComplete(null);
            }
        });
    }

    public void addComment(final Comment comment, final Dao.AddCommentListener listener) {
        String id = commentRef.document().getId();
        comment.setId(id);
        commentRef.document(id).set(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    listener.onComplete(comment);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Tag", "Comment add onFailure: " + e.getMessage());
                    listener.onComplete(null);
                }
            });
    }


    public void deleteComment(final String commentId, final Dao.DeleteCommentListener listener){
        commentRef.document(commentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onComplete(aVoid);
            }
        });
    }

    public void getComment(final String postId, final String commentId, final Dao.GetCommentListener listener){

//        getPost(postId, new Dao.GetPostListener() {
//            @Override
//            public void onComplete(Post post) {
//                String userId = post.getAuthor().getId();
//                db.collection("Users").document(userId).collection("Posts").document(postId)
//                        .collection("Comments").document(commentId).get()
//                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                final Comment comment = documentSnapshot.toObject(Comment.class);
//                                comment.setId(documentSnapshot.getId());
//                                getUser(comment.getUserId(), new Dao.GetUserDetailsListener() {
//                                    @Override
//                                    public void onComplete(User user) {
//                                        comment.setAuthor(user);
//                                        listener.onComplete(comment);
//                                    }
//                                });
//                            }
//                        });
//            }
//        });
    }


}

//package com.example.Sportify.dal;
//
//import androidx.annotation.NonNull;
//
//import com.example.Sportify.models.Post;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.EventListener;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.FirebaseFirestoreSettings;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.annotation.Nullable;
//
//public class FirebaseDao {
//
//    FirebaseFirestore db;
//
//    public FirebaseDao() {
//        db = FirebaseFirestore.getInstance();
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(false).build();
//        db.setFirestoreSettings(settings);
//    }
//
//    public void getAllPosts(final Dao.GetAllPostsListener listener) {
//        db.collection("posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                List<Post> data = new ArrayList<>();
//                if (e != null) {
//                    listener.onComplete(data);
//                    return;
//                }
//                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                    Post post = doc.toObject(Post.class);
//                    data.add(post);
//                }
//                listener.onComplete(data);
//            }
//        });
//    }
//
//    public void addPost(Post post) {
//        db.collection("posts").document(post.getId()).set(post);
//    }
//
//    interface GetPostsListener {
//        void onComplete(Post post);
//    }
//
//    public void getPost(String id, final GetPostsListener listener) {
//        db.collection("students").document(id).get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot snapshot = task.getResult();
//                            Post post = snapshot.toObject(Post.class);
//                            listener.onComplete(post);
//                            return;
//                        }
//                        listener.onComplete(null);
//                    }
//                });
//    }
//}

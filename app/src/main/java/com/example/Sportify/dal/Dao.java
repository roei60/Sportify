//package com.example.Sportify.dal;
//
//import com.example.Sportify.models.Post;
//
//import java.util.List;
//
//public class Dao {
//    final public static Dao instance = new Dao();
//
//    //ModelSql modelSql;
//    FirebaseDao firebaseDao;
//    private Dao() {
//        //modelSql = new ModelSql();
//        firebaseDao = new FirebaseDao();
////        for (int i = 0; i < 10; i++) {
////            Student st = new Student("" + i, "demo " + i, "image url");
////            addStudent(st);
////        }
//    }
//
//
//    public interface GetAllPostsListener {
//        void onComplete(List<Post> data);
//    }
//    public void getAllPosts(GetAllPostsListener listener) {
//        firebaseDao.getAllPosts(listener);
//    }
//
//    public void addStudent(Post post) {
//        firebaseDao.addPost(post);
//    }
//
//}

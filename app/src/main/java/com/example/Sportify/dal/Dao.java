package com.example.Sportify.dal;

import com.example.Sportify.models.Post;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class Dao {
    final public static Dao instance = new Dao();

    //ModelSql modelSql;
    FirebaseDao firebaseDao;
    private Dao() {
        //modelSql = new ModelSql();
        firebaseDao = new FirebaseDao();

//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();
//        System.out.println(dateFormat.format(date));
//        Post p1 = new Post("Avi Levi", "my first post!", date);
//        Post p2 = new Post("Avi Levi", "my second post!", date);
//        addPost(p1);
//        addPost(p2);

//        for (int i = 0; i < 10; i++) {
//            Student st = new Student("" + i, "demo " + i, "image url");
//            addStudent(st);
//        }
    }


    public interface GetAllPostsListener {
        void onComplete(List<Post> data);
    }
    public void getAllPosts(GetAllPostsListener listener) {
        firebaseDao.getAllPosts(listener);
    }

    public void addPost(Post post) {
        firebaseDao.addPost(post);
    }

}

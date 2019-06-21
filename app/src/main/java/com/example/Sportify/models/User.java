package com.example.Sportify.models;

import java.util.Vector;

public class User {
    private String mName;
    private String mEmail;
    private String mToken;
   // private Vector<Card> mCards;
   // private Vector<Share> mShares;
    private String mImageUri;

    public String getName(){  return this.mName;   }
    public String getEmail(){ return this.mEmail; }
    public String getToken() { return this.mToken; }
    //public Vector<Card> getCards(){  return this.mCards;   }
    //public Vector<Share> getShares() { return this.mShares; }
    public String getImageUri() {return this.mImageUri;}

    public User(String name, String email, String token, String imageUri){
        mName = name;
        mEmail = email;
        mToken = token;
        mImageUri = imageUri;
    }

  //  public User(String name, String email, Boolean isPro, String token, String imageUri, Vector<Card> cards){
    //    this(name, email, isPro, token, imageUri);
    //    mCards = cards;
   // }

 //   public User(String name, String email, Boolean isPro, String token, String imageUri, Vector<Card> cards, Vector<Share> shares){
   //     this(name, email, isPro, token, imageUri);
       // mCards = cards;
     //   mShares = shares;
    //}
}

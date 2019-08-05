package com.example.Sportify.Activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.Sportify.R;
import com.example.Sportify.dal.Dao;
import com.example.Sportify.models.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity  implements
        NavigationView.OnNavigationItemSelectedListener {

    public Toolbar toolbar;

    public DrawerLayout drawerLayout;

    public NavController navController;

    public NavigationView navigationView;

    public TextView Menu_EmailText;
    public TextView Menu_NameTxt;
    public ImageView Menu_ProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setDrawerEnabled(false);
    }

    public void enableNavigation(boolean enabled){
        setupNavigation();
        setDrawerEnabled(enabled);
        if(enabled)
            UpdateUserData();
    }

    private void UpdateUserData() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Dao.instance.getUserDetails(currentUser.getUid(), new Dao.GetUserDetailsListener() {
            @Override
            public void onComplete(User user) {
                Dao.instance.setCurrentUser(user);
                user.setEmail(currentUser.getEmail());
                Menu_EmailText.setText(currentUser.getEmail());
                Menu_NameTxt.setText(user.getName());
                Picasso.with(MainActivity.this).load(user.getImageUri()).fit().into(Menu_ProfilePicture);

            }
        });
    }

    private void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(lockMode);
    }

    // Setting Up One Time Navigation
    private void setupNavigation() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.navigationView);

        navController = Navigation.findNavController(this, R.id.main_navigation);

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);

        this.Menu_ProfilePicture = findViewById(R.id.Header_ProfilePicture);
        this.Menu_EmailText=findViewById(R.id.Header_EmailTxt);
        this.Menu_NameTxt=findViewById(R.id.Header_NameTxt);
        Menu_ProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                navController.navigate(R.id.profileFragment);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.main_navigation), drawerLayout);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        menuItem.setChecked(true);

        drawerLayout.closeDrawers();

        int id = menuItem.getItemId();

        switch (id) {

            case R.id.first:
                navController.navigate(R.id.profileFragment);
                break;

            case R.id.second:
                navController.navigate(R.id.postFragment);
                break;

            case R.id.third:
                navController.navigate(R.id.commentsFragment);
                break;

        }
        return true;

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

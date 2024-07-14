package com.example.taskjmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;




import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskjmanager.views.DashboardActivity;
import com.example.taskjmanager.views.LandingActivity;
import com.example.taskjmanager.views.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splashscreen extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent;
                if (currentUser != null) {
                    // User is authenticated, go to MainActivity (Dashboard)
                    intent = new Intent(Splashscreen.this, DashboardActivity.class);
                } else {
                    // User is not authenticated, go to LoginActivity
                    intent = new Intent(Splashscreen.this, LandingActivity.class);
                }
                startActivity(intent);
                finish(); // Close the splash screen activity
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }
}


package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef= database.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser!=null) {

            SystemClock.sleep(2000);

            if(currentUser.getDisplayName()!=null){
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }else {
                Intent editProfileIntent = new Intent(SplashActivity.this, EditProfileActivity.class);
                editProfileIntent.putExtra("type",1);
                editProfileIntent.putExtra("phone",mAuth.getCurrentUser().getPhoneNumber());
                startActivity(editProfileIntent);
                finish();
            }

        }else {
            SystemClock.sleep(2000);
            Intent loginIntent = new Intent(SplashActivity.this,OtpActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }
}

package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private Button login;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myRef;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef= database.getReference();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        final Intent intent = new Intent( this,MainActivity.class);

        if(firebaseAuth.getCurrentUser()!=null){

            startActivity(intent);
            finish();
            return;
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email.getText().toString().isEmpty()){
                    email.setError("requied");
                    return;
                }else {
                    email.setError(null);
                }


                if(password.getText().toString().isEmpty()){
                    password.setError("requied");
                    return;
                }else {
                    password.setError(null);
                }

                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            if(firebaseAuth.getCurrentUser().getDisplayName()!=null){
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

                            }else {
                                Intent intent = new Intent(LoginActivity.this, EditProfileActivity.class);
                                intent.putExtra("type",1);
                                intent.putExtra("phone","0999999999");
                                startActivity(intent);
                                finish();
                            }

//
//                            Intent editProfileIntent = new Intent(LoginActivity.this, EditProfileActivity.class);
//                            editProfileIntent.putExtra("type",1);
//                            startActivity(editProfileIntent);
//                            finish();
//                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(LoginActivity.this, "Shomething Went Wrong", Toast.LENGTH_SHORT).show();

                        }

                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

    }
}

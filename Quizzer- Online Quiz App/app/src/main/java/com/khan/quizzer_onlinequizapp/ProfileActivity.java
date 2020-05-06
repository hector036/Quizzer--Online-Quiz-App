package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.khan.quizzer_onlinequizapp.MainActivity.decodedBytes;
import static com.khan.quizzer_onlinequizapp.MainActivity.firstName;
import static com.khan.quizzer_onlinequizapp.MainActivity.institute;
import static com.khan.quizzer_onlinequizapp.MainActivity.lastName;
import static com.khan.quizzer_onlinequizapp.MainActivity.phone;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName;
    private TextView instituteName;
    private TextView contactNumber;
    private FloatingActionButton editProfileBtn;
    private Button signOutBtn;
    private CircleImageView imageView;
    private boolean isFabEnable = false;

//    private String firstName;
//    private String lastName;
//    private String institute;
 //   private String phone;
//    private Bitmap decodedBitmap;

    private FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth= FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileName = findViewById(R.id.profile_name);
        instituteName = findViewById(R.id.institue_text);
        contactNumber = findViewById(R.id.contact_number);
        imageView = findViewById(R.id.profile_image_2);
        editProfileBtn = findViewById(R.id.edit_profile_btn);
        signOutBtn = findViewById(R.id.sign_out);

        if(institute.isEmpty()){
            getUserDetails();
        }else {
            profileName.setText(firstName+" "+lastName);
            instituteName.setText(institute);
            contactNumber.setText(phone);
            decodedBytes = Base64.decode( MainActivity.url, Base64.DEFAULT);
            Glide.with(ProfileActivity.this).load( MainActivity.decodedBytes).placeholder(R.drawable.profile_edit).into(imageView);
            isFabEnable = true;

        }

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFabEnable){
                    Intent intent = new Intent(ProfileActivity.this,EditProfileActivity.class);
                    intent.putExtra("firstName",firstName);
                    intent.putExtra("lastName",lastName);
                    intent.putExtra("instituteName",institute);
                    intent.putExtra("phone",phone);
                    intent.putExtra("type",2);
                    startActivity(intent);
                }

            }
        });

    }

    private void getUserDetails(){
        myRef.child("Users").child(Objects.requireNonNull(auth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!isFinishing()){
                    firstName = dataSnapshot.child("firstname").getValue().toString();
                    lastName = dataSnapshot.child("lastName").getValue().toString();
                    institute = dataSnapshot.child("instituteName").getValue().toString();
                    phone = dataSnapshot.child("phone").getValue().toString();
                    MainActivity.url = dataSnapshot.child("url").getValue().toString();

                    profileName.setText(firstName+" "+lastName);
                    instituteName.setText(institute);
                    contactNumber.setText(phone);
                    decodedBytes = Base64.decode( MainActivity.url, Base64.DEFAULT);
                    Glide.with(ProfileActivity.this).load( MainActivity.decodedBytes).placeholder(R.drawable.profile_edit).into(imageView);
                    isFabEnable = true;
               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ProfileActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(institute.isEmpty()){
            getUserDetails();
        }else {
            profileName.setText(firstName+" "+lastName);
            instituteName.setText(institute);
            contactNumber.setText(phone);
            decodedBytes = Base64.decode( MainActivity.url, Base64.DEFAULT);
            Glide.with(ProfileActivity.this).load( MainActivity.decodedBytes).placeholder(R.drawable.profile_edit).into(imageView);
            isFabEnable = true;
        }

    }
}

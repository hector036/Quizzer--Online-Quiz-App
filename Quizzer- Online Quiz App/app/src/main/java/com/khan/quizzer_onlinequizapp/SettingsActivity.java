package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.khan.quizzer_onlinequizapp.SettingsModel.NOTIFICATION_ITEM;
import static com.khan.quizzer_onlinequizapp.SettingsModel.SETTINGS_HEADER;
import static com.khan.quizzer_onlinequizapp.SettingsModel.SETTINGS_ITEM_WITH_OUT_SWITCH;
import static com.khan.quizzer_onlinequizapp.SettingsModel.SETTINGS_ITEM_WITH_OUT_SWITCH_AND_WITH_URL;
import static com.khan.quizzer_onlinequizapp.SettingsModel.SETTINGS_ITEM_WITH_SWITCH;

public class SettingsActivity extends AppCompatActivity {

    public static final int FROM_NOTIFICATION = 1;
    public static final int FROM_SETTINGS = 0;
    private static final String privacy_link = "https://anunad001.github.io/anunad/";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private FirebaseAuth mAuth;

    private int type;
    private SettingsAdapter adapter;
    private List<SettingsModel> list = new ArrayList<>();
    private ProgressBar progressBar;
    private LinearLayout blankFigureLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        progressBar = findViewById(R.id.progress_bar_settings);
        blankFigureLinearLayout = findViewById(R.id.black_image_linear_layout_noti);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        type = getIntent().getIntExtra("type", 0);

        RecyclerView recyclerView = findViewById(R.id.settings_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new SettingsAdapter(list);
        recyclerView.setAdapter(adapter);



        if (type == FROM_NOTIFICATION) {
            getSupportActionBar().setTitle("Notification");
            getNotification();

        } else {
            getSupportActionBar().setTitle("Settings");
            getSettings();

        }


    }

    private void getNotification() {
        progressBar.setVisibility(View.VISIBLE);
        myRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("notifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String notificationTitle = dataSnapshot1.child("notificationTitle").getValue().toString();
                    String notificationDescription = dataSnapshot1.child("notificationDescription").getValue().toString();
                    String notificationType = dataSnapshot1.child("notificationType").getValue().toString();
                    String notificationPhotoUrl = dataSnapshot1.child("notificationPhotoUrl").getValue().toString();
                    String notificationLink = dataSnapshot1.child("notificationLink").getValue().toString();
                    long notificationTime = (long) dataSnapshot1.child("notificationTime").getValue();
                    list.add(0, new SettingsModel(NOTIFICATION_ITEM, notificationType, notificationTitle, notificationDescription, notificationPhotoUrl, notificationLink, notificationTime));

                }

                if(list.isEmpty()){
                    blankFigureLinearLayout.setVisibility(View.VISIBLE);
                }else {
                    blankFigureLinearLayout.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                getSharedPreferences("Notifications", MODE_PRIVATE).edit().putInt("" + mAuth.getCurrentUser().getUid(), (int) dataSnapshot.getChildrenCount()).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SettingsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getSettings() {
        boolean isDarkMode = getSharedPreferences("Settings:"+"Dark Mode", MODE_PRIVATE).getBoolean(mAuth.getCurrentUser().getUid(), false);
        boolean isWeeklyTestEnable = getSharedPreferences("Settings:"+"Weekly Test Notification", MODE_PRIVATE).getBoolean(mAuth.getCurrentUser().getUid(), true);
        boolean isEducationalNewsEnable = getSharedPreferences("Settings:"+"Educational News", MODE_PRIVATE).getBoolean(mAuth.getCurrentUser().getUid(), true);

        list.add(new SettingsModel(SETTINGS_ITEM_WITH_SWITCH, R.drawable.s1, "Dark Mode", isDarkMode));
        list.add(new SettingsModel(SETTINGS_HEADER, "Notification Center"));
        list.add(new SettingsModel(SETTINGS_ITEM_WITH_SWITCH, R.drawable.mp2, "Weekly Test Notification", isWeeklyTestEnable));
        list.add(new SettingsModel(SETTINGS_ITEM_WITH_SWITCH, R.drawable.s3, "Educational News", isEducationalNewsEnable));
        list.add(new SettingsModel(SETTINGS_HEADER, "Others"));
        list.add(new SettingsModel(SETTINGS_ITEM_WITH_OUT_SWITCH_AND_WITH_URL, R.drawable.s4, "Privacy And Policy", privacy_link));
        list.add(new SettingsModel(SETTINGS_ITEM_WITH_OUT_SWITCH, R.drawable.s5, "Version 1.1.6", true));

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

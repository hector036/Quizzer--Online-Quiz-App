package com.khan.quizzer_onlinequizapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class TestsActivity extends AppCompatActivity {

    private static int FROM_WEEKLYTEST = 0;
    private static int FROM_CENTRALTEST = 1;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ListView listView;
    private TestAdapter testAdapter;
    ArrayList<Test> tests = new ArrayList<>();
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        type = getIntent().getIntExtra("type", 0);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(TestsActivity.this, OtpActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
            finish();
        }
        progressBar = findViewById(R.id.progress_bar_test);
        progressBar.setVisibility(View.VISIBLE);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        listView = findViewById(R.id.test_listview);

        if (type == FROM_WEEKLYTEST) {
            toolbar.setTitle("Weekly Test");
            testAdapter = new TestAdapter(TestsActivity.this, tests, FROM_WEEKLYTEST);
            listView.setAdapter(testAdapter);
            getWeeklyTest();
        } else {
            toolbar.setTitle("Central Test");
            testAdapter = new TestAdapter(TestsActivity.this, tests, FROM_CENTRALTEST);
            listView.setAdapter(testAdapter);
            getCentralTest();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getWeeklyTest() {
        myRef.child("tests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tests.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Test t = new Test();
                    t.setSetId(snapshot.getKey());
                    t.setName(snapshot.child("name").getValue().toString());
                    t.setDateTime((Long) snapshot.child("date").getValue());
                    t.setDescription(snapshot.child("description").getValue().toString());
                    tests.add(t);

                }

                testAdapter.dataList = tests;
                testAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                Log.e("The read success: ", "su" + tests.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.e("The read failed: ", databaseError.getMessage());
            }
        });
    }

    public void getCentralTest() {
        myRef.child("central_tests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tests.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Test t = new Test();
                    t.setSetId(snapshot.getKey());
                    t.setName(snapshot.child("name").getValue().toString());
                    t.setDateTime((Long) snapshot.child("date").getValue());
                    t.setDescription(snapshot.child("description").getValue().toString());
                    t.setSocreInc(Double.parseDouble(snapshot.child("scoreInc").getValue().toString()));
                    t.setSocreDe(Double.parseDouble(snapshot.child("scoreDe").getValue().toString()));
                    t.setTime((Long) snapshot.child("time").getValue());
                    tests.add(t);

                }

                testAdapter.dataList = tests;
                testAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                Log.e("The read success: ", "su" + tests.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.e("The read failed: ", databaseError.getMessage());
            }
        });
    }

}

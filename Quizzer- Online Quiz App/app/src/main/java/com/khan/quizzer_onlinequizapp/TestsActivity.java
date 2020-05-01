package com.khan.quizzer_onlinequizapp;


import android.annotation.SuppressLint;
import android.app.DirectAction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
//import com.example.test_quiz.NotificationActivity.NotificationService;
//import com.example.test_quiz.R;
//import com.example.test_quiz.Model.Question;
//import com.example.test_quiz.Model.Test;
//import com.example.test_quiz.Results_section.ResultsAdminDetailed;
import com.devs.readmoreoption.ReadMoreOption;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class TestsActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
  //  private AVLoadingIndicatorView avLoadingIndicatorView;
    private ListView listView;
    private TestAdapter testAdapter;
    private int lastPos = -1;
    private String setId;
    ArrayList<Test> tests=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
      //  avLoadingIndicatorView = findViewById(R.id.loader1);
       // avLoadingIndicatorView.setVisibility(View.VISIBLE);
       // avLoadingIndicatorView.show();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Weekly Exam");
        database= FirebaseDatabase.getInstance();
        myRef=database.getReference();
        listView=findViewById(R.id.test_listview);
        testAdapter=new TestAdapter(TestsActivity.this,tests);
        listView.setAdapter(testAdapter);
        getQues();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
       // stopService(new Intent(Tests.this, NotificationService.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getQues(){
        //addListenerForSingleValueEvent
        myRef.child("tests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tests.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Test t=new Test();
                    t.setSetId( snapshot.getKey());
                    t.setName(snapshot.child("name").getValue().toString());
//                    t.setTime(Long.parseLong(snapshot.child("Time").getValue().toString()));
                    t.setDate(snapshot.child("date").getValue().toString());
                    t.setStartTime(snapshot.child("startTime").getValue().toString());
                    t.setDescription(snapshot.child("description").getValue().toString());
                //    ArrayList<Question> ques=new ArrayList<>();
                  //  for (DataSnapshot qSnap:snapshot.child("Questions").getChildren()){
                 //       ques.add(qSnap.getValue(Question.class));
                  //  }
                 //   t.setQuestions(ques);
                    tests.add(t);

                }
               // Collections.reverse(tests);

                testAdapter.dataList=tests;
                testAdapter.notifyDataSetChanged();
                //avLoadingIndicatorView.setVisibility(View.GONE);
               // avLoadingIndicatorView.hide();
                Log.e("The read success: " ,"su"+tests.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
              //  avLoadingIndicatorView.setVisibility(View.GONE);
              //  avLoadingIndicatorView.hide();
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }



}

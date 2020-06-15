package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuesbankQuestionsActivity extends AppCompatActivity {

    private String setId;
    private String categoryName;
    private String url = "";

    private RecyclerView recyclerView;
    private QuesbankQuestionAdapter adapter;
    private Button submitBtn;
    public static List<QuestionModel> list;
    private DatabaseReference myRef;
    private ProgressBar progressBar;
    private double score = 0;
    private double socreInc;
    private double socreDe;
    private long timer;
    CountDownTimer countdownTimer;
    private MenuItem counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quesbank_questions);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        categoryName = getIntent().getStringExtra("category");
        setId = getIntent().getStringExtra("setId");
        socreInc = getIntent().getDoubleExtra("scoreInc", 1);
        socreDe = getIntent().getDoubleExtra("scoreDe", 0);
        timer = getIntent().getLongExtra("time", 1);
        getSupportActionBar().setTitle(categoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view_);
        submitBtn = findViewById(R.id.submit_btn);
        progressBar = findViewById(R.id.progressBar);

        countdownTimer = new CountDownTimer(timer * 60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {

                long Minutes = millisUntilFinished / (60 * 1000) % 60;
                long Seconds = millisUntilFinished / 1000 % 60;

                counter.setTitle(String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds));
            }

            public void onFinish() {
                counter.setTitle("Time is Over");

                Toast.makeText(QuesbankQuestionsActivity.this, "Time is Over", Toast.LENGTH_SHORT).show();
                //  Intent scoreIntent = new Intent(QuesbankQuestionsActivity.this, ScoreActivity.class);
                //   scoreIntent.putExtra("score", score);
                // scoreIntent.putExtra("total", totalQues);
                //   startActivity(scoreIntent);
                //   finish();
                return;
            }
        };

        myRef = FirebaseDatabase.getInstance().getReference();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        adapter = new QuesbankQuestionAdapter(list, categoryName, new QuesbankQuestionAdapter.CheckResult() {
            @Override
            public void onRadioCheck(int position, String yourAns, int ansPosition) {
                if (yourAns.equals(list.get(position).getCorrectAns())) {
                    score = score + socreInc;
                } else {
                    score = score - socreDe;
                }
                list.get(position).setAnsPosition(ansPosition);
            }

        });

        recyclerView.setAdapter(adapter);

        getData(categoryName, setId);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(QuesbankQuestionsActivity.this, "Your Score is " + score, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(String categoryName, final String setId) {
        progressBar.setVisibility(View.VISIBLE);
        myRef.child("SETS").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String id = dataSnapshot1.getKey();
                    String question = dataSnapshot1.child("question").getValue().toString();
                    String a = dataSnapshot1.child("optionA").getValue().toString();
                    String b = dataSnapshot1.child("optionB").getValue().toString();
                    String c = dataSnapshot1.child("optionC").getValue().toString();
                    String d = dataSnapshot1.child("optionD").getValue().toString();
                    String corentAns = dataSnapshot1.child("correctAns").getValue().toString();
                    String e;
                    if (dataSnapshot1.child("optionE").exists()) {
                        e = dataSnapshot1.child("optionE").getValue().toString();
                    } else {
                        e = "";
                    }
                    if (dataSnapshot1.child("url").exists()) {
                        url = dataSnapshot1.child("url").getValue().toString();
                    } else {
                        url = "";
                    }
                    list.add(new QuestionModel(id, question, a, b, c, d, e, corentAns, setId, url));
                }
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                countdownTimer.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuesbankQuestionsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                finish();
            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.timer_menu, menu);
        counter = menu.findItem(R.id.timer);

        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
    }
}

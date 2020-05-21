package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class AnsSheetActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookmarksAdater adater;

    private List<QuestionModel> list;
    private TextView scoreBoard;
    private Button viewMerit;
    private LinearLayout scoreBoardLayout;
    private int score,total;
    private int isScoreBoard,isEvaluation;
    private String testName,setId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ans_sheet);

        Toolbar toolbar = findViewById(R.id.toolbar);

        loadAds();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Answer Sheet");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.rv_answersheet);
        scoreBoard = findViewById(R.id.score_board);
        scoreBoardLayout = findViewById(R.id.score_board_layout);
        viewMerit = findViewById(R.id.view_merit);

        score = getIntent().getIntExtra("score",0);
        total = getIntent().getIntExtra("total",0);
        testName = getIntent().getStringExtra("test");
        setId = getIntent().getStringExtra("setId");

        scoreBoard.setText("Your Score : "+score+" / "+total);

        isScoreBoard = getIntent().getIntExtra("isScoreBoard",0);
        isEvaluation = getIntent().getIntExtra("isEvaluation",1);

        if(isScoreBoard==1){
            scoreBoardLayout.setVisibility(View.VISIBLE);
        }else {
            scoreBoardLayout.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if(isEvaluation==1){
            adater = new BookmarksAdater(QuestionsActivity.listAns,1);
        }else {
            adater = new BookmarksAdater(QuestionsActivity.listAns,3);
        }
        recyclerView.setAdapter(adater);

        viewMerit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnsSheetActivity.this,ViewResultsActivity.class);
                intent.putExtra("test", testName);
                intent.putExtra("setId", setId);
                startActivity(intent);
                //finish();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadAds() {

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


}

package com.example.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.quizzer_onlinequizapp.QuestionsActivity.FILE_NAME;
import static com.example.quizzer_onlinequizapp.QuestionsActivity.KEY_NAME;

public class AnsSheetActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private List<QuestionModel> list;


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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();


        list.add(new QuestionModel("A man presses more weight on earth at :","Sitting position","Standing Position","Lying Position","None of these","Standing Position",1));
        list.add(new QuestionModel("A piece of ice is dropped in a vesel containing kerosene. When ice melts, the level of kerosene will","Rise","Fall","Remain Same","None of these","Fall",1));
        list.add(new QuestionModel("Young's modulus is the property of ","Gas only","Both Solid and Liquid","Liquid only","Solid only","Solid only",1));
        list.add(new QuestionModel("An artificial Satellite revolves round the Earth in circular orbit, which quantity remains constant?","Angular Momentum","Linear Velocity","Angular Displacement","None of these","Angular Momentum",1));
        list.add(new QuestionModel("With the increase of pressure, the boiling point of any substance","Increases","Decreases","Remains Same","Becomes zero","Increases",1));


        BookmarksAdater adater = new BookmarksAdater(list,1);
        recyclerView.setAdapter(adater);
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

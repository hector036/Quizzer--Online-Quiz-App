package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.khan.quizzer_onlinequizapp.CategoriesActivity.FROM_ADMISSION_PREPARATION_ACTIVITY;
import static com.khan.quizzer_onlinequizapp.CategoriesActivity.FROM_ADMISSION_QUESTION_BANK_ACTIVITY;
import static com.khan.quizzer_onlinequizapp.CategoriesActivity.FROM_BOARD_QUESTION_BANK_ACTIVITY;
import static com.khan.quizzer_onlinequizapp.CategoriesActivity.FROM_SUBJECT_WISE_ACTIVITY;

public class ChapterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button modelTestBtn;
    private List<TestClass> chapterList;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        type = getIntent().getIntExtra("type", 0);

        recyclerView = findViewById(R.id.recycler_view);
        modelTestBtn = findViewById(R.id.model_test_btn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        chapterList = CategoriesActivity.list.get(getIntent().getIntExtra("position", 0)).getChapters();

        Collections.sort(chapterList, new Comparator<TestClass>() {
            @Override
            public int compare(TestClass o1, TestClass o2) {
                return (int) (o1.getOrder() - o2.getOrder());
            }
        });

        if (type == FROM_SUBJECT_WISE_ACTIVITY) {
            ChapterAdapter adapter = new ChapterAdapter(FROM_SUBJECT_WISE_ACTIVITY, chapterList, getIntent().getStringExtra("title"));
            recyclerView.setAdapter(adapter);
        }else if(type == FROM_ADMISSION_QUESTION_BANK_ACTIVITY){
            ChapterAdapter adapter = new ChapterAdapter(FROM_ADMISSION_QUESTION_BANK_ACTIVITY, chapterList, getIntent().getStringExtra("title"));
            recyclerView.setAdapter(adapter);
        }else if(type == FROM_BOARD_QUESTION_BANK_ACTIVITY){
            ChapterAdapter adapter = new ChapterAdapter(FROM_BOARD_QUESTION_BANK_ACTIVITY, chapterList, getIntent().getStringExtra("title"));
            recyclerView.setAdapter(adapter);
        }else if(type == FROM_ADMISSION_PREPARATION_ACTIVITY){
            ChapterAdapter adapter = new ChapterAdapter(FROM_ADMISSION_PREPARATION_ACTIVITY, chapterList, getIntent().getStringExtra("title"));
            recyclerView.setAdapter(adapter);
        }

        if (type == FROM_SUBJECT_WISE_ACTIVITY) {
            modelTestBtn.setVisibility(View.VISIBLE);
            modelTestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setIntent = new Intent(ChapterActivity.this, SetsActivity.class);
                    setIntent.putExtra("title", getIntent().getStringExtra("title"));
                    setIntent.putExtra("position", getIntent().getIntExtra("position", 0));
                    setIntent.putExtra("key", getIntent().getStringExtra("key"));
                    startActivity(setIntent);
                }
            });
        } else {
            modelTestBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

}

package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ChapterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button modelTestBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        recyclerView = findViewById(R.id.recycler_view);
        modelTestBtn = findViewById(R.id.model_test_btn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        ChapterAdapter adapter = new ChapterAdapter(CategoriesActivity.list.get(getIntent().getIntExtra("position",0)).getChapters(),getIntent().getStringExtra("title"));
        recyclerView.setAdapter(adapter);

        modelTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setIntent = new Intent( ChapterActivity.this,SetsActivity.class);
                setIntent.putExtra("title",getIntent().getStringExtra("title"));
                setIntent.putExtra("position",getIntent().getIntExtra("position",0));
                setIntent.putExtra("key",getIntent().getStringExtra("key"));
                startActivity(setIntent);
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

}

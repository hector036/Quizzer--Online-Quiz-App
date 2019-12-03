package com.example.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class QuestionsActivity extends AppCompatActivity {


    public static final String FILE_NAME = "QUIZZER";
    public static final String KEY_NAME = "QUESTIONS";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();


    private TextView question,noIndicator,totalQuestion;
    private FloatingActionButton bookmarks;
    private LinearLayout optionContrainer;
    private Button shareBtn, nextBtn;
    private  List<QuestionModel> list;
    private int position;
    private int score = 0;
    private int count=0;
    private String category;
    private int setNo;
    private boolean isTabed=false;
    private int totalQues;
    CountDownTimer countdownTimer;
    private int matchedQuestionPosition;

    private List<QuestionModel> bookmarksList;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        //Toolbar toolbar = findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        loadAds();

        question = findViewById(R.id.questions);
        noIndicator = findViewById(R.id.no_indicator);
        bookmarks = findViewById(R.id.bookmark_button);
        optionContrainer = findViewById(R.id.options_contrainer);
        shareBtn = findViewById(R.id.share_button);
        nextBtn = findViewById(R.id.next_button);
        totalQuestion = findViewById(R.id.total_question);

        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        getBookmarks();

        bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modelMatch()){
                    bookmarksList.remove(matchedQuestionPosition);
                    bookmarks.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                    Toast.makeText(QuestionsActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                }else {
                    bookmarksList.add(list.get(position));
                    bookmarks.setImageDrawable(getDrawable(R.drawable.bookmark));
                    Toast.makeText(QuestionsActivity.this, "Bookmarked", Toast.LENGTH_SHORT).show();


                }
            }
        });


        category = getIntent().getStringExtra("category");
        setNo = getIntent().getIntExtra("setNo",1);




        list = new ArrayList<>();

        list.add(new QuestionModel("A man presses more weight on earth at :","Sitting position","Standing Position","Lying Position","None of these","Standing Position",1));
        list.add(new QuestionModel("A piece of ice is dropped in a vesel containing kerosene. When ice melts, the level of kerosene will","Rise","Fall","Remain Same","None of these","Fall",1));
        list.add(new QuestionModel("Young's modulus is the property of ","Gas only","Both Solid and Liquid","Liquid only","Solid only","Solid only",1));
        list.add(new QuestionModel("An artificial Satellite revolves round the Earth in circular orbit, which quantity remains constant?","Angular Momentum","Linear Velocity","Angular Displacement","None of these","Angular Momentum",1));
        list.add(new QuestionModel("With the increase of pressure, the boiling point of any substance","Increases","Decreases","Remains Same","Becomes zero","Increases",1));

        totalQues = list.size();
        totalQuestion.setText("Total Questions: "+totalQues);

        countdownTimer= new CountDownTimer(2*60*1000, 1000) {

            public void onTick(long millisUntilFinished) {

                long Minutes = millisUntilFinished / (60 * 1000) % 60;
                long Seconds = millisUntilFinished / 1000 % 60;

                noIndicator.setText(String.format("%02d", Minutes)+":"+String.format("%02d", Seconds));
            }

            public void onFinish() {
                noIndicator.setText("Time is Over");

                Toast.makeText(QuestionsActivity.this, "Time is Over", Toast.LENGTH_SHORT).show();
                Intent scoreIntent = new Intent(QuestionsActivity.this,ScoreActivity.class);
                scoreIntent.putExtra("score",score);
                scoreIntent.putExtra("total",totalQues);
                startActivity(scoreIntent);
                finish();
                return;
            }
        }.start();


        tempFunction();

//        myRef.child("SETS").child(category).child("questions").orderByChild("setNo").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
//                    list.add(snapshot.getValue(QuestionModel.class));
//                }
//
//                if(list.size() > 0){
//                    for(int i=0;i<4;i++){
//                        optionContrainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                isTabed = true;
//                                checkAnswer((Button) v);
//                            }
//                        });
//                    }
//
//                    playAnim(question,0,list.get(position).getQuestion());
//
//                    nextBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            enableOption(true);
//                            if(isTabed){
//                                list.remove(position);
//                            }else {
//                                position++;
//
//                            }
//                            if(position == list.size()){
//                                //
//                                Intent scoreIntent = new Intent(QuestionsActivity.this,ScoreActivity.class);
//                                scoreIntent.putExtra("score",score);
//                                scoreIntent.putExtra("total",list.size());
//                                startActivity(scoreIntent);
//                                finish();
//                                return;
//                            }
//                            count=0;
//
//                            playAnim(question,0,list.get(position).getQuestion());
//                            isTabed = false;
//
//                        }
//
//                    });
//
//                    shareBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            enableOption(true);
//                            position--;
//                            if(position == list.size()){
//                                //
//
//                                return;
//                            }
//                            count=0;
//
//                            playAnim(question,0,list.get(position).getQuestion());
//                        }
//                    });
//                }else {
//                    finish();
//                    Toast.makeText(QuestionsActivity.this, "No Ques", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                Toast.makeText(QuestionsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });



    }


    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }

    private void playAnim(final View view, final int value, final String data){

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                if(value ==0 && count< 4){

                    String option="";

                    if(count == 0){
                        option = list.get(position).getOptionA();
                    }else if(count == 1){
                        option = list.get(position).getOptionB();

                    }else if(count == 2){
                        option = list.get(position).getOptionC();

                    }else if(count == 3){
                        option = list.get(position).getOptionD();

                    }

                    playAnim(optionContrainer.getChildAt(count),0,option);
                    count++;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(value == 0){

                    try {
                        ((TextView)view).setText(data);
                       // noIndicator.setText(position+1+"/"+list.size());
                        if(modelMatch()){
                            bookmarks.setImageDrawable(getDrawable(R.drawable.bookmark));
                        }else {
                            bookmarks.setImageDrawable(getDrawable(R.drawable.bookmark_border));

                        }


                    }catch (ClassCastException ex){
                        ((Button)view).setText(data);

                    }
                    view.setTag(data);
                    playAnim(view,1,data);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void checkAnswer(Button selectOption){

        enableOption(false);

        if(selectOption.getText().toString().equals(list.get(position).getCorrectAns())){
            //

            selectOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55D394")));
            score++;

        }else {
            //
            selectOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55D394")));
           //Button currectoption = (Button) optionContrainer.findViewWithTag(list.get(position).getCorrectAns());
            //currectoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55D394")));

        }
    }

    private void enableOption(boolean enable){
        for(int i= 0; i< 4; i++){
            optionContrainer.getChildAt(i).setEnabled(enable);
            if(enable){
                optionContrainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#989898")));

            }
        }
    }

    private void tempFunction(){
        for(int i=0;i<4;i++){
            optionContrainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isTabed = true;
                    checkAnswer((Button) v);
                }
            });
        }

        playAnim(question,0,list.get(position).getQuestion());

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableOption(true);
                if(isTabed){
                    list.remove(position);
                }else {
                    position++;

                }
                if(position>0){
                    shareBtn.setEnabled(true);
                    shareBtn.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")));

                }
                if(position == list.size()-1){
                    nextBtn.setText("Submit");
                }
                if(position == list.size()){
                    //
                    Intent scoreIntent = new Intent(QuestionsActivity.this,ScoreActivity.class);
                    scoreIntent.putExtra("score",score);
                    System.out.println("Check score"+score);
                    scoreIntent.putExtra("total",totalQues);
                    startActivity(scoreIntent);
                    finish();
                    return;
                }
                count=0;

                playAnim(question,0,list.get(position).getQuestion());
                isTabed = false;

            }

        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableOption(true);
                if(isTabed){
                    list.remove(position);
                }
                position--;
                if(position == 0){
                    shareBtn.setEnabled(false);
                    shareBtn.setTextColor(ColorStateList.valueOf(Color.parseColor("#45ffffff")));
                }
                if(position !=list.size()){
                    nextBtn.setText("Next");
                }
                if(position == list.size()-1){
                    nextBtn.setText("Submit");
                }
                count=0;

                playAnim(question,0,list.get(position).getQuestion());
                isTabed = false;
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
    }

    private void getBookmarks(){
        String json = preferences.getString(KEY_NAME,"");
        Type type =new  TypeToken<List<QuestionModel>>(){}.getType();

        bookmarksList = gson.fromJson(json,type);

        if(bookmarksList == null){
            bookmarksList = new ArrayList<>();
        }
    }

    private boolean modelMatch(){

        boolean matched = false;
        int i=0;
         for(QuestionModel model: bookmarksList){
             if(model.getQuestion().equals(list.get(position).getQuestion())
             && model.getCorrectAns().equals(list.get(position).getCorrectAns())
             && model.getSetNo() == list.get(position).getSetNo()
             ){
                 matched=true;
                 matchedQuestionPosition = i;
             }
             i++;

         }

         return matched;
    }

    private void storeBookmarks(){

        String json = gson.toJson(bookmarksList);

        editor.putString(KEY_NAME,json);

        editor.commit();

    }


    private void loadAds() {

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}

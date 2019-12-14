package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.ProgressDialog;
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


    private TextView question, noIndicator, totalQuestion;
    private FloatingActionButton bookmarks;
    private LinearLayout optionContrainer;
    private Button shareBtn, nextBtn;
    private List<QuestionModel> list;
    public static List<QuestionModel> listAns;


    private int position;
    private int score = 0;
    private int count = 0;
    private String category;
    private String setId;
    private boolean isTabed = false;
    private int totalQues;
    CountDownTimer countdownTimer;
    private int matchedQuestionPosition;

    private List<QuestionModel> bookmarksList;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    private ProgressDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

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

        loadingDialog = new ProgressDialog(this, R.style.dialogStyle);
        loadingDialog.setMessage("Loading...");
        loadingDialog.setCancelable(true);

        getBookmarks();

        bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelMatch()) {
                    bookmarksList.remove(matchedQuestionPosition);
                    bookmarks.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                    Toast.makeText(QuestionsActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                } else {
                    bookmarksList.add(list.get(position));
                    bookmarks.setImageDrawable(getDrawable(R.drawable.bookmark));
                    Toast.makeText(QuestionsActivity.this, "Bookmarked", Toast.LENGTH_SHORT).show();


                }
            }
        });


        category = getIntent().getStringExtra("category");
        setId = getIntent().getStringExtra("setId");


        list = new ArrayList<>();
        listAns = new ArrayList<>();

        countdownTimer = new CountDownTimer(25 * 60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {

                long Minutes = millisUntilFinished / (60 * 1000) % 60;
                long Seconds = millisUntilFinished / 1000 % 60;

                noIndicator.setText(String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds));
            }

            public void onFinish() {
                noIndicator.setText("Time is Over");

                Toast.makeText(QuestionsActivity.this, "Time is Over", Toast.LENGTH_SHORT).show();
                Intent scoreIntent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                scoreIntent.putExtra("score", score);
                scoreIntent.putExtra("total", totalQues);
                startActivity(scoreIntent);
                finish();
                return;
            }
        }.start();


        loadingDialog.show();
        myRef.child("SETS").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i = 1;

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String id = dataSnapshot1.getKey();
                    String question = "Q " + i + ".   " + dataSnapshot1.child("question").getValue().toString();
                    String a = dataSnapshot1.child("optionA").getValue().toString();
                    String b = dataSnapshot1.child("optionB").getValue().toString();
                    String c = dataSnapshot1.child("optionC").getValue().toString();
                    String d = dataSnapshot1.child("optionD").getValue().toString();
                    String correctAns = dataSnapshot1.child("correctAns").getValue().toString();

                    list.add(new QuestionModel(id, question, a, b, c, d, correctAns, setId));
                    listAns.add(new QuestionModel(id, question, a, b, c, d, "Ans. " + correctAns, setId));
                    i++;
                }

                if (list.size() > 0) {

                    totalQues = list.size();
                    totalQuestion.setText("Total Questions: " + totalQues);

                    tempFunction();
                    loadingDialog.dismiss();

                } else {
                    loadingDialog.dismiss();
                    finish();
                    Toast.makeText(QuestionsActivity.this, "The Questions will be uploaded soon", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(QuestionsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }

    private void playAnim(final View view, final int value, final String data) {

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                if (value == 0 && count < 4) {

                    String option = "";

                    if (count == 0) {
                        option = list.get(position).getOptionA();
                    } else if (count == 1) {
                        option = list.get(position).getOptionB();

                    } else if (count == 2) {
                        option = list.get(position).getOptionC();

                    } else if (count == 3) {
                        option = list.get(position).getOptionD();

                    }

                    playAnim(optionContrainer.getChildAt(count), 0, option);
                    count++;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (value == 0) {

                    try {
                        ((TextView) view).setText(data);
                        // noIndicator.setText(position+1+"/"+list.size());
                        if (modelMatch()) {
                            bookmarks.setImageDrawable(getDrawable(R.drawable.bookmark));
                        } else {
                            bookmarks.setImageDrawable(getDrawable(R.drawable.bookmark_border));

                        }


                    } catch (ClassCastException ex) {
                        ((Button) view).setText(data);

                    }
                    view.setTag(data);
                    playAnim(view, 1, data);
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

    private void checkAnswer(Button selectOption) {

        enableOption(false);

        if (selectOption.getText().toString().equals(list.get(position).getCorrectAns())) {
            //

            selectOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55D394")));
            score++;

        } else {
            //
            selectOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55D394")));
            //Button currectoption = (Button) optionContrainer.findViewWithTag(list.get(position).getCorrectAns());
            //currectoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55D394")));

        }
    }

    private void enableOption(boolean enable) {
        for (int i = 0; i < 4; i++) {
            optionContrainer.getChildAt(i).setEnabled(enable);
            if (enable) {
                optionContrainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#989898")));

            }
        }
    }

    private void tempFunction() {
        for (int i = 0; i < 4; i++) {
            optionContrainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isTabed = true;
                    checkAnswer((Button) v);
                }
            });
        }

        playAnim(question, 0, list.get(position).getQuestion());

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableOption(true);
                if (isTabed) {
                    list.remove(position);
                } else {
                    position++;

                }
                if (position > 0) {
                    shareBtn.setEnabled(true);
                    shareBtn.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")));

                }
                if (position == list.size() - 1) {
                    nextBtn.setText("Submit");
                }
                if (position == list.size()) {
                    //
                    Intent scoreIntent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                    scoreIntent.putExtra("score", score);
                    System.out.println("Check score" + score);
                    scoreIntent.putExtra("total", totalQues);
                    startActivity(scoreIntent);
                    finish();
                    return;
                }
                count = 0;

                playAnim(question, 0, list.get(position).getQuestion());
                isTabed = false;

            }

        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableOption(true);
                if (isTabed) {
                    list.remove(position);
                }
                position--;
                if (position == 0) {
                    shareBtn.setEnabled(false);
                    shareBtn.setTextColor(ColorStateList.valueOf(Color.parseColor("#45ffffff")));
                }
                if (position != list.size()) {
                    nextBtn.setText("Next");
                }
                if (position == list.size() - 1) {
                    nextBtn.setText("Submit");
                }
                count = 0;

                playAnim(question, 0, list.get(position).getQuestion());
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

    private void getBookmarks() {
        String json = preferences.getString(KEY_NAME, "");
        Type type = new TypeToken<List<QuestionModel>>() {
        }.getType();

        bookmarksList = gson.fromJson(json, type);

        if (bookmarksList == null) {
            bookmarksList = new ArrayList<>();
        }
    }

    private boolean modelMatch() {

        boolean matched = false;
        int i = 0;
        for (QuestionModel model : bookmarksList) {
            if (model.getQuestion().equals(list.get(position).getQuestion())
                    && model.getCorrectAns().equals(list.get(position).getCorrectAns())
                    && model.getSet() == list.get(position).getSet()
            ) {
                matched = true;
                matchedQuestionPosition = i;
            }
            i++;

        }

        return matched;
    }

    private void storeBookmarks() {

        String json = gson.toJson(bookmarksList);

        editor.putString(KEY_NAME, json);

        editor.commit();

    }


    private void loadAds() {

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}

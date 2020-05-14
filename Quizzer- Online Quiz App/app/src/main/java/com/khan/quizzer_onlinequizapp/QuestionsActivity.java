package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import katex.hourglass.in.mathlib.MathView;

import static com.khan.quizzer_onlinequizapp.MainActivity.institute;

public class QuestionsActivity extends AppCompatActivity {

    public static final String FILE_NAME = "QUIZZER";
    public static final String KEY_NAME = "QUESTIONS";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private FirebaseAuth mAuth;

    private TextView noIndicator, totalQuestion, quesText;
    private ImageButton bookmarks;
    private LinearLayout optionContrainer, layoutQuestion;
    private Button shareBtn, nextBtn, viewAnsSheet, startBtn;
    private List<QuestionModel> list;
    public static List<QuestionModel> listAns;
    private List<QuestionModel> bookmarksList;

    private int position;
    private int score = 0;
    private int type;
    private int count = 0;
    private String category;
    private String url = "";
    String instituteNAme = "", temp;
    private String setId, testName;
    private boolean isTabed = false;
    private int totalQues = 0;
    private int matchedQuestionPosition;


    CountDownTimer countdownTimer;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    private ProgressDialog loadingDialog, loadingDialogUploadScore;
    private AlertDialog alertDialog;
    private boolean btnEnable = false;
    private boolean finishActivity = true;
    private boolean isQuestionLoaded = false;

    private MathView question;
    private ImageView figure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);


        mAuth = FirebaseAuth.getInstance();

        question = findViewById(R.id.questions);
        figure = findViewById(R.id.figure_ques);
        noIndicator = findViewById(R.id.no_indicator);
        bookmarks = findViewById(R.id.bookmark_button);
        optionContrainer = findViewById(R.id.options_contrainer);
        layoutQuestion = findViewById(R.id.layoutQuestion);
        shareBtn = findViewById(R.id.share_button);
        nextBtn = findViewById(R.id.next_button);
        totalQuestion = findViewById(R.id.total_question);

        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        getBookmarks();

        loadingDialog = new ProgressDialog(this, R.style.dialogStyle);
        loadingDialog.setMessage("Loading...");
        loadingDialog.setCancelable(true);
        loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!isQuestionLoaded) {
                    loadingDialog.dismiss();
                    finish();
                }
            }
        });


        loadingDialogUploadScore = new ProgressDialog(this, R.style.dialogStyle);
        loadingDialogUploadScore.setMessage("Uploading Score...");
        loadingDialogUploadScore.setCancelable(false);


        type = getIntent().getIntExtra("type", 0);
        category = getIntent().getStringExtra("category");
        setId = getIntent().getStringExtra("setId");
        testName = getIntent().getStringExtra("test");


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

                if (type == 1) {
                    Intent scoreIntent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                    scoreIntent.putExtra("score", score);
                    scoreIntent.putExtra("total", totalQues);
                    startActivity(scoreIntent);
                    finish();
                    return;
                } else {
                    uploadScore();
                    return;
                }


            }
        };


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
                    if (dataSnapshot1.child("url").exists()) {
                        url = dataSnapshot1.child("url").getValue().toString();
                    } else {
                        url = "";
                    }

                    list.add(new QuestionModel(id, question, a, b, c, d, correctAns, setId, url));
                    list.get(i - 1).setInitPosition(i - 1);

                    listAns.add(new QuestionModel(id, question, a, b, c, d, correctAns, setId, url));
                    i++;
                }

                isQuestionLoaded = true;

                if (type == 1) {

                    if (list.size() > 0) {
                        totalQues = list.size();
                        totalQuestion.setText("Questions: " + totalQues);
                        tempFunction();
                        setTimer();
                        countdownTimer.start();
                        loadingDialog.dismiss();

                    } else {
                        loadingDialog.dismiss();
                        finish();
                        Toast.makeText(QuestionsActivity.this, "The Questions will be uploaded soon", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    myRef.child("Results").child(setId).child(Objects.requireNonNull(mAuth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (!isFinishing()) {
                                    showDialog();
                                }
                            } else {
                                if (list.size() > 0) {
                                    totalQues = list.size();
                                    totalQuestion.setText("Questions: " + totalQues);
                                    tempFunction();
                                    setTimer();
                                    countdownTimer.start();
                                    loadingDialog.dismiss();

                                } else {
                                    loadingDialog.dismiss();
                                    finish();
                                    Toast.makeText(QuestionsActivity.this, "The Questions will be uploaded soon", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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

    private void playAnim(final View view, final int value, final String data, final String figure) {

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

                    playAnim(optionContrainer.getChildAt(count), 0, option, "");
                    count++;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (value == 0) {

                    try {
                        if (isTex(data)) {
                            ((TextView) ((LinearLayout) view).getChildAt(1)).setVisibility(View.GONE);
                            ((MathView) ((LinearLayout) view).getChildAt(0)).setVisibility(View.VISIBLE);
                            ((MathView) ((LinearLayout) view).getChildAt(0)).setDisplayText(data);
                        } else {
                            ((MathView) ((LinearLayout) view).getChildAt(0)).setVisibility(View.GONE);
                            ((TextView) ((LinearLayout) view).getChildAt(1)).setVisibility(View.VISIBLE);
                            ((TextView) ((LinearLayout) view).getChildAt(1)).setText(data);
                        }

                        if (figure.isEmpty()) {
                            ((ImageView) ((LinearLayout) view).getChildAt(2)).setVisibility(View.GONE);
                        } else {
                            ((ImageView) ((LinearLayout) view).getChildAt(2)).setVisibility(View.VISIBLE);
                            Glide.with(QuestionsActivity.this).load(figure).placeholder(R.drawable.profile_edit).into((ImageView) ((LinearLayout) view).getChildAt(2));
                        }
                        if (modelMatch()) {
                            bookmarks.setImageDrawable(getDrawable(R.drawable.bookmark));
                        } else {
                            bookmarks.setImageDrawable(getDrawable(R.drawable.bookmark_border));

                        }


                    } catch (ClassCastException ex) {
                        ((Button) view).setText(data);

                    }
                    view.setTag(data);
                    playAnim(view, 1, data, "");
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

            selectOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55D394")));
            listAns.get(list.get(position).getInitPosition()).setYourAns(selectOption.getText().toString());
            score++;

        } else {

            listAns.get(list.get(position).getInitPosition()).setYourAns(selectOption.getText().toString());

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

        playAnim(layoutQuestion, 0, list.get(position).getQuestion(), list.get(position).getUrl());

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

                    //shareBtn.setEnabled(true);
                    btnEnable = true;
                    shareBtn.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")));

                }
                if (position == list.size() - 1) {
                    nextBtn.setText("Submit");
                }
                if (position == list.size()) {
                    //

                    if (type == 1) {
                        Intent scoreIntent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                        scoreIntent.putExtra("score", score);
                        scoreIntent.putExtra("total", totalQues);
                        startActivity(scoreIntent);
                        finish();
                        return;
                    } else {
                        uploadScore();
                        return;
                    }

                }
                count = 0;

                playAnim(layoutQuestion, 0, list.get(position).getQuestion(), list.get(position).getUrl());
                isTabed = false;

            }

        });


        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnEnable) {
                    enableOption(true);
                    if (isTabed) {
                        list.remove(position);
                    }
                    position--;
                    if (position == 0) {
                        btnEnable = false;
                        shareBtn.setTextColor(ColorStateList.valueOf(Color.parseColor("#45ffffff")));
                    }
                    if (position != list.size()) {
                        nextBtn.setText("Next  ›");
                    }
                    if (position == list.size() - 1) {
                        nextBtn.setText("Submit");
                    }
                    count = 0;

                    playAnim(layoutQuestion, 0, list.get(position).getQuestion(), list.get(position).getUrl());
                    isTabed = false;
                } else {
                    Toast.makeText(QuestionsActivity.this, "No Previous Questions Available", Toast.LENGTH_SHORT).show();
                }


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
            if (model.getQuestion().equals((list.get(position).getQuestion()))
                    && model.getCorrectAns().equals(list.get(position).getCorrectAns())
                    && model.getSet().equals(list.get(position).getSet())
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

    @Override
    public void onBackPressed() {

        alertDialog = new AlertDialog.Builder(this, R.style.dialogStyle)
                .setTitle("Quit Exam")
                .setPositiveButton("QUIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type == 1) {
                            finish();
                        } else {
                            dialog.dismiss();
                            uploadScore();
                        }
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        if (type == 1) {
            alertDialog.setMessage("Are you sure you want to quit this exam?");
        } else {
            alertDialog.setMessage("Your score is " + score + " " + "Are you sure you want to quit this exam?");
        }
        alertDialog.show();
    }

    private void uploadScore() {

        final Map<String, Object> map = new HashMap<>();

        map.put("name", mAuth.getCurrentUser().getDisplayName());

        loadingDialogUploadScore.show();
        if (institute == null || institute.isEmpty()) {

            myRef.child("Users").child(Objects.requireNonNull(mAuth.getUid())).child("instituteName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    instituteNAme = dataSnapshot.getValue().toString();
                    map.put("instituteName", instituteNAme);
                    map.put("score", score);
                    setResultDetails(map);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            instituteNAme = institute;
            map.put("instituteName", instituteNAme);
            map.put("score", score);
            setResultDetails(map);
        }


    }

    private void setResultDetails(Map<String, Object> map) {

        database.getReference().child("Results").child(setId).child(Objects.requireNonNull(mAuth.getUid())).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(QuestionsActivity.this, AnsSheetActivity.class);
                    intent.putExtra("score", score);
                    intent.putExtra("total", totalQues);
                    intent.putExtra("isScoreBoard", 1);
                    intent.putExtra("test", testName);
                    intent.putExtra("setId", setId);
                    startActivity(intent);
                    finish();
                    loadingDialogUploadScore.dismiss();

                } else {
                    loadingDialogUploadScore.dismiss();
                    Toast.makeText(QuestionsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void showDialog() {

        View dialogView = LayoutInflater.from(this).inflate(R.layout.question_dialog, null);
        /**/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (finishActivity) {
                    loadingDialog.dismiss();
                    finish();
                }
            }
        });

        alertDialog.show();

        viewAnsSheet = dialogView.findViewById(R.id.view_ans_sheet);
        startBtn = dialogView.findViewById(R.id.start_question);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = 1;
                finishActivity = false;

                alertDialog.cancel();


                if (list.size() > 0) {

                    totalQues = list.size();
                    totalQuestion.setText("Questions: " + totalQues);

                    tempFunction();
                    setTimer();
                    countdownTimer.start();
                    loadingDialog.dismiss();


                } else {
                    loadingDialog.dismiss();
                    finish();
                    Toast.makeText(QuestionsActivity.this, "The Questions will be uploaded soon", Toast.LENGTH_SHORT).show();
                }

            }
        });

        viewAnsSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionsActivity.this, AnsSheetActivity.class);
                intent.putExtra("isScoreBoard", 0);
                intent.putExtra("isEvaluation", 0);
                startActivity(intent);
                finish();
            }
        });

    }

    private void setTimer() {
        countdownTimer = new CountDownTimer(totalQues * 60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {

                long Minutes = millisUntilFinished / (60 * 1000) % 60;
                long Seconds = millisUntilFinished / 1000 % 60;

                noIndicator.setText(String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds));
            }

            public void onFinish() {
                noIndicator.setText("Time is Over");

                Toast.makeText(QuestionsActivity.this, "Time is Over", Toast.LENGTH_SHORT).show();

                if (type == 1) {
                    Intent scoreIntent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                    scoreIntent.putExtra("score", score);
                    scoreIntent.putExtra("total", totalQues);
                    startActivity(scoreIntent);
                    finish();
                    return;
                } else {
                    uploadScore();
                    return;
                }


            }
        };
    }

    private boolean isTex(String str) {
        if (str.contains("\\(") || str.contains("\\)") || str.contains("$") || str.contains("\\begin") || str.contains("\\end") || str.contains("\\ (") || str.contains("\\ )")) {
            return true;
        } else
            return false;

    }

    private String cutString(String str) {
        if (str.charAt(3) == '.') {
            return str.substring(3);
        } else if (str.charAt(4) == '.') {
            return str.substring(4);
        } else if (str.charAt(5) == '.') {
            return str.substring(5);
        }else
            return str.substring(6);

    }
}
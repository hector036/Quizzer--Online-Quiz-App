package com.khan.quizzer_onlinequizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.khan.quizzer_onlinequizapp.MainActivity.institute;

public class QuesbankQuestionsActivity extends AppCompatActivity {

    private static int FROM_CENTRAL_TEST = 1;
    private static int FROM_QUESBANK = 0;
    private String setId;
    private String categoryName;
    private String url = "";

    private RecyclerView recyclerView;
    private QuesbankQuestionAdapter adapter;
    private Button submitBtn;
    private List<QuestionModel> list;
    public static List<QuestionModel> listQAns = new ArrayList<>();
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private Button viewAnsSheet, startBtn;

    private double score = 0;
    private double socreInc;
    private double socreDe;
    private long timer;
    CountDownTimer countdownTimer;
    private AlertDialog alertDialog;
    private TextView time, totalQuestion;
    private boolean isQuestionLoaded = false;
    private ProgressDialog loadingDialog, loadingDialogUploadScore;
    private int type;
    private boolean finishActivity = true;
    private String instituteNAme = "", testName;
    private int totalQues = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quesbank_questions);
        mAuth = FirebaseAuth.getInstance();

        categoryName = getIntent().getStringExtra("category");
        setId = getIntent().getStringExtra("setId");
        socreInc = getIntent().getDoubleExtra("scoreInc", 1);
        socreDe = getIntent().getDoubleExtra("scoreDe", 0);
        timer = getIntent().getLongExtra("time", 1);

        recyclerView = findViewById(R.id.recycler_view_);
        submitBtn = findViewById(R.id.submit_btn);
        time = findViewById(R.id.no_indicator);
        totalQuestion = findViewById(R.id.total_question);
        type = getIntent().getIntExtra("type", 0);
        testName = getIntent().getStringExtra("test");


        myRef = FirebaseDatabase.getInstance().getReference();

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
                listQAns.get(position).setYourAns(yourAns);
            }

        });

        recyclerView.setAdapter(adapter);

        getData(categoryName, setId);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(QuesbankQuestionsActivity.this, "Your Score is " + score, Toast.LENGTH_SHORT).show();

                if (type == FROM_CENTRAL_TEST) {
                    uploadScore();
                    return;
                } else {
                    Intent scoreIntent = new Intent(QuesbankQuestionsActivity.this, ScoreActivity.class);
                    scoreIntent.putExtra("score", score);
                    scoreIntent.putExtra("total", list.size());
                    scoreIntent.putExtra("type", 1);
                    startActivity(scoreIntent);
                    finish();
                    return;
                }
            }
        });
    }

    private void setUpTimer() {
        countdownTimer = new CountDownTimer(timer * 60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {

                long Minutes = millisUntilFinished / (60 * 1000);
                long Seconds = millisUntilFinished / 1000 % 60;

                time.setText(String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds));
                totalQuestion.setText("Full Marks: " + list.size());

            }

            public void onFinish() {
                time.setText("Time is over");

                Toast.makeText(QuesbankQuestionsActivity.this, "Time is Over", Toast.LENGTH_SHORT).show();
                if (type == FROM_CENTRAL_TEST) {
                    uploadScore();
                    return;
                } else {
                    Intent scoreIntent = new Intent(QuesbankQuestionsActivity.this, ScoreActivity.class);
                    scoreIntent.putExtra("score", score);
                    scoreIntent.putExtra("total", list.size());
                    scoreIntent.putExtra("type", 1);
                    startActivity(scoreIntent);
                    finish();
                    return;
                }
            }
        };
        countdownTimer.start();

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
        myRef.child("Results").child(setId).child(Objects.requireNonNull(mAuth.getUid())).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(QuesbankQuestionsActivity.this, AnsSheetActivity.class);
                    intent.putExtra("score", score);
                    intent.putExtra("total", totalQues);
                    intent.putExtra("isScoreBoard", 1);
                    intent.putExtra("test", testName);
                    intent.putExtra("setId", setId);
                    intent.putExtra("type", 1);

                    startActivity(intent);
                    finish();
                    loadingDialogUploadScore.dismiss();

                } else {
                    loadingDialogUploadScore.dismiss();
                    Toast.makeText(QuesbankQuestionsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void getData(String categoryName, final String setId) {
        list.clear();
        listQAns.clear();
        loadingDialog.show();
        myRef.child("SETS").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 1;
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
                    listQAns.add(new QuestionModel(id, "Q " + i + ".   " + question, a, b, c, d, e, corentAns, setId, url));
                    i++;
                }

                isQuestionLoaded = true;
                totalQues = list.size();
                if (type == FROM_QUESBANK) {
                    if (list.size() > 0) {
                        adapter.notifyDataSetChanged();
                        setUpTimer();
                        loadingDialog.dismiss();
                    } else {
                        loadingDialog.dismiss();
                        finish();
                        Toast.makeText(QuesbankQuestionsActivity.this, "The Questions will be uploaded soon", Toast.LENGTH_SHORT).show();
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
                                    adapter.notifyDataSetChanged();
                                    setUpTimer();
                                    loadingDialog.dismiss();

                                } else {
                                    loadingDialog.dismiss();
                                    finish();
                                    Toast.makeText(QuesbankQuestionsActivity.this, "The Questions will be uploaded soon", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(QuesbankQuestionsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    private void showDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.question_dialog, null);
        /**/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

                type = FROM_QUESBANK;
                finishActivity = false;

                alertDialog.cancel();

                if (list.size() > 0) {
                    adapter.notifyDataSetChanged();
                    setUpTimer();
                    loadingDialog.dismiss();

                } else {
                    loadingDialog.dismiss();
                    finish();
                    Toast.makeText(QuesbankQuestionsActivity.this, "The Questions will be uploaded soon", Toast.LENGTH_SHORT).show();
                }

            }
        });

        viewAnsSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuesbankQuestionsActivity.this, AnsSheetActivity.class);
                intent.putExtra("isScoreBoard", 0);
                intent.putExtra("isEvaluation", 0);
                intent.putExtra("type", 1);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

        alertDialog = new AlertDialog.Builder(this, R.style.alertDialogStyle)
                .setTitle("Quit Exam")
                .setPositiveButton("QUIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type == FROM_CENTRAL_TEST) {
                            dialog.dismiss();
                            uploadScore();
                        } else {
                            finish();
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
    }
}

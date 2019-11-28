package com.example.quizzer_onlinequizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    private TextView question,noIndicator;
    private FloatingActionButton bookmarks;
    private LinearLayout optionContrainer;
    private Button shareBtn, nextBtn;
    private  List<QuestionModel> list;
    private int position;
    private int score = 0;

    private int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        question = findViewById(R.id.questions);
        noIndicator = findViewById(R.id.no_indicator);
        bookmarks = findViewById(R.id.bookmark_button);
        optionContrainer = findViewById(R.id.options_contrainer);
        shareBtn = findViewById(R.id.share_button);
        nextBtn = findViewById(R.id.next_button);

        list = new ArrayList<>();

        list.add(new QuestionModel("Question 1","A","B","C","D","D"));
        list.add(new QuestionModel("Question 2","A","B","C","D","D"));
        list.add(new QuestionModel("Question 3","A","B","C","D","C"));
        list.add(new QuestionModel("Question 4","A","B","C","D","D"));
        list.add(new QuestionModel("Question 5","A","B","C","D","D"));
        list.add(new QuestionModel("Question 6","A","B","C","D","D"));
        list.add(new QuestionModel("Question 7","A","B","C","D","A"));

        for(int i=0;i<4;i++){
            optionContrainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer((Button) v);
                }
            });
        }

        playAnim(question,0,list.get(position).getQuestion());
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableOption(true);
                position++;
                if(position == list.size()){
                    //

                    return;
                }
                count=0;

                playAnim(question,0,list.get(position).getQuestion());
            }
        });

    }

    private void playAnim(final View view, final int value,final String data){

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
                        noIndicator.setText(position+1+"/"+list.size());


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

        if(selectOption.equals(list.get(position).getCorrectAns())){
            //
            score++;
            selectOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55D394")));

        }else {
            //
            selectOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
           Button currectoption = (Button) optionContrainer.findViewWithTag(list.get(position).getCorrectAns());
            currectoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#55D394")));

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
}

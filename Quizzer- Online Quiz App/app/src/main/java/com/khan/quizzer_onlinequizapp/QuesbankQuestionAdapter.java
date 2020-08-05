package com.khan.quizzer_onlinequizapp;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import katex.hourglass.in.mathlib.MathView;

public class QuesbankQuestionAdapter extends RecyclerView.Adapter<QuesbankQuestionAdapter.Viewholder> {

    private List<QuestionModel> list = new ArrayList<>();
    private String category;
    private CheckResult checkResult;

    public QuesbankQuestionAdapter(List<QuestionModel> list, String category, CheckResult checkResult) {
        this.list = list;
        this.category = category;
        this.checkResult = checkResult;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quesbankquestion_item, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        String question = list.get(position).getQuestion();
        String url = list.get(position).getUrl();
        String optionA = list.get(position).getOptionA();
        String optionB = list.get(position).getOptionB();
        String optionC = list.get(position).getOptionC();
        String optionD = list.get(position).getOptionD();
        String optionE = list.get(position).getOptionE();
        int ansPosition = list.get(position).getAnsPosition();

        holder.setData(question, url, optionA, optionB, optionC, optionD, optionE, ansPosition, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class Viewholder extends RecyclerView.ViewHolder {

        private MathView optionA, optionB, optionC, optionD, optionE;
        private TextView optionAText, optionBText, optionCText, optionDText, optionEText;
        private RadioButton radioA, radioB, radioC, radioD, radioE;
        private ImageView figureA, figureB, figureC, figureD, figureE;
        private MathView question;
        private TextView quesText;
        private ImageView figure;
        private LinearLayout linearLayoutOptionE;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.question);
            quesText = itemView.findViewById(R.id.quesText);
            figure = itemView.findViewById(R.id.figure);

            optionA = itemView.findViewById(R.id.opA);
            optionB = itemView.findViewById(R.id.opB);
            optionC = itemView.findViewById(R.id.opC);
            optionD = itemView.findViewById(R.id.opD);
            optionE = itemView.findViewById(R.id.opE);

            optionAText = itemView.findViewById(R.id.opAText);
            optionBText = itemView.findViewById(R.id.opBText);
            optionCText = itemView.findViewById(R.id.opCText);
            optionDText = itemView.findViewById(R.id.opDText);
            optionEText = itemView.findViewById(R.id.opEText);

            figureA = itemView.findViewById(R.id.figureA);
            figureB = itemView.findViewById(R.id.figureB);
            figureC = itemView.findViewById(R.id.figureC);
            figureD = itemView.findViewById(R.id.figureD);
            figureE = itemView.findViewById(R.id.figureE);

            radioA = itemView.findViewById(R.id.radioA);
            radioB = itemView.findViewById(R.id.radioB);
            radioC = itemView.findViewById(R.id.radioC);
            radioD = itemView.findViewById(R.id.radioD);
            radioE = itemView.findViewById(R.id.radioE);

            linearLayoutOptionE = itemView.findViewById(R.id.linearLayoutOptionE);

        }

        private void setData(String question, String url, final String optionA, String optionB, String optionC, String optionD, String optionE, int ansPosition, final int position) {

            //////////////// radio button /////////////////
            if (ansPosition == 0) {
                this.radioA.setChecked(true);
                this.radioB.setChecked(false);
                this.radioC.setChecked(false);
                this.radioD.setChecked(false);
                this.radioE.setChecked(false);
                disableAllOption();
            } else if (ansPosition == 1) {
                this.radioA.setChecked(false);
                this.radioB.setChecked(true);
                this.radioC.setChecked(false);
                this.radioD.setChecked(false);
                this.radioE.setChecked(false);
                disableAllOption();
            } else if (ansPosition == 2) {
                this.radioA.setChecked(false);
                this.radioB.setChecked(false);
                this.radioC.setChecked(true);
                this.radioD.setChecked(false);
                this.radioE.setChecked(false);
                disableAllOption();
            } else if (ansPosition == 3) {
                this.radioA.setChecked(false);
                this.radioB.setChecked(false);
                this.radioC.setChecked(false);
                this.radioD.setChecked(true);
                this.radioE.setChecked(false);
                disableAllOption();

            } else if (ansPosition == 4) {
                this.radioA.setChecked(false);
                this.radioB.setChecked(false);
                this.radioC.setChecked(false);
                this.radioD.setChecked(false);
                this.radioE.setChecked(true);
                disableAllOption();
            }

            //////////////// radio button /////////////////

            //////////////// Question /////////////////


            if (isTex(question)) {
                this.quesText.setVisibility(View.GONE);
                this.question.setVisibility(View.VISIBLE);
                this.question.setDisplayText(position + 1 + ". " + question);
            } else {
                this.question.setVisibility(View.GONE);
                this.quesText.setVisibility(View.VISIBLE);
                this.quesText.setText(position + 1 + ". " + question);
            }

            //////////////// Question /////////////////


            //////////////// Figure /////////////////

            if (url.isEmpty()) {
                figure.setVisibility(View.GONE);
            } else {
                figure.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext()).load(url).placeholder(R.drawable.profile1_home).into(figure);
            }

            //////////////// Figure /////////////////

            //////////////// Option /////////////////

            if (isValidUrl(optionA)) {
                this.optionA.setVisibility(View.GONE);
                this.optionAText.setVisibility(View.GONE);
                figureA.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext()).load(optionA).placeholder(R.drawable.profile1_home).into(figureA);
            } else if (isTex(optionA)) {
                this.optionAText.setVisibility(View.GONE);
                figureA.setVisibility(View.GONE);
                this.optionA.setVisibility(View.VISIBLE);
                this.optionA.setDisplayText(optionA);
            } else {
                figureA.setVisibility(View.GONE);
                this.optionA.setVisibility(View.GONE);
                this.optionAText.setVisibility(View.VISIBLE);
                this.optionAText.setText(optionA);
            }

            if (isValidUrl(optionB)) {
                this.optionBText.setVisibility(View.GONE);
                this.optionB.setVisibility(View.GONE);
                figureB.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext()).load(optionB).placeholder(R.drawable.profile1_home).into(figureB);
            } else if (isTex(optionB)) {
                this.optionBText.setVisibility(View.GONE);
                figureB.setVisibility(View.GONE);
                this.optionB.setVisibility(View.VISIBLE);
                this.optionB.setDisplayText(optionB);
            } else {
                this.optionB.setVisibility(View.GONE);
                figureB.setVisibility(View.GONE);
                this.optionBText.setVisibility(View.VISIBLE);
                this.optionBText.setText(optionB);
            }

            if (isValidUrl(optionC)) {
                this.optionCText.setVisibility(View.GONE);
                this.optionC.setVisibility(View.GONE);
                figureC.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext()).load(optionC).placeholder(R.drawable.profile1_home).into(figureC);
            } else if (isTex(optionC)) {
                this.optionCText.setVisibility(View.GONE);
                figureC.setVisibility(View.GONE);
                this.optionC.setVisibility(View.VISIBLE);
                this.optionC.setDisplayText(optionC);
            } else {
                this.optionC.setVisibility(View.GONE);
                figureC.setVisibility(View.GONE);
                this.optionCText.setVisibility(View.VISIBLE);
                this.optionCText.setText(optionC);
            }

            if (isValidUrl(optionD)) {
                this.optionDText.setVisibility(View.GONE);
                this.optionD.setVisibility(View.GONE);
                figureD.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext()).load(optionD).placeholder(R.drawable.profile1_home).into(figureD);
            } else if (isTex(optionD)) {
                this.optionDText.setVisibility(View.GONE);
                figureD.setVisibility(View.GONE);
                this.optionD.setVisibility(View.VISIBLE);
                this.optionD.setDisplayText(optionD);
            } else {
                this.optionD.setVisibility(View.GONE);
                figureD.setVisibility(View.GONE);
                this.optionDText.setVisibility(View.VISIBLE);
                this.optionDText.setText(optionD);
            }

            if (optionE.equals("")) {
                linearLayoutOptionE.setVisibility(View.GONE);
            } else {
                linearLayoutOptionE.setVisibility(View.VISIBLE);

                if (isValidUrl(optionE)) {
                    this.optionEText.setVisibility(View.GONE);
                    this.optionE.setVisibility(View.GONE);
                    figureE.setVisibility(View.VISIBLE);
                    Glide.with(itemView.getContext()).load(optionE).placeholder(R.drawable.profile1_home).into(figureE);
                } else if (isTex(optionE)) {
                    this.optionEText.setVisibility(View.GONE);
                    figureE.setVisibility(View.GONE);
                    this.optionE.setVisibility(View.VISIBLE);
                    this.optionE.setDisplayText(optionE);
                } else {
                    this.optionE.setVisibility(View.GONE);
                    figureE.setVisibility(View.GONE);
                    this.optionEText.setVisibility(View.VISIBLE);
                    this.optionEText.setText(optionE);
                }
            }

            //////////////// Option /////////////////

            //////////////// Check results /////////////////

            if (ansPosition == -1) {

                enableAllOptions();

                radioA.setOnClickListener(new View.OnClickListener() {
                    private MathView optionA;

                    @Override
                    public void onClick(View v) {
                        radioA.setChecked(true);
                        checkResult.onRadioCheck(position, list.get(position).getOptionA(), 0);
                        disableAllOption();
                    }
                });

                radioB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        radioB.setChecked(true);
                        checkResult.onRadioCheck(position, list.get(position).getOptionB(), 1);
                        disableAllOption();
                    }
                });

                radioC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        radioC.setChecked(true);
                        checkResult.onRadioCheck(position, list.get(position).getOptionC(), 2);
                        disableAllOption();
                    }
                });

                radioD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        radioD.setChecked(true);
                        checkResult.onRadioCheck(position, list.get(position).getOptionD(), 3);
                        disableAllOption();
                    }
                });

                radioE.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        radioE.setChecked(true);
                        checkResult.onRadioCheck(position, list.get(position).getOptionE(), 4);
                        disableAllOption();
                    }
                });

            }

            //////////////// Check results /////////////////


        }

        private void enableAllOptions() {
            this.radioA.setChecked(false);
            this.radioB.setChecked(false);
            this.radioC.setChecked(false);
            this.radioD.setChecked(false);
            this.radioE.setChecked(false);

            radioA.setEnabled(true);
            radioB.setEnabled(true);
            radioC.setEnabled(true);
            radioD.setEnabled(true);
            radioE.setEnabled(true);
        }

        private void disableAllOption() {
            this.optionA.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
            this.optionAText.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
            this.optionB.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
            this.optionBText.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
            this.optionC.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
            this.optionCText.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
            this.optionD.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
            this.optionDText.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
            this.optionE.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
            this.optionEText.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));

            radioA.setEnabled(false);
            radioB.setEnabled(false);
            radioC.setEnabled(false);
            radioD.setEnabled(false);
            radioE.setEnabled(false);

            radioA.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
            radioB.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
            radioC.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
            radioD.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
            radioE.setTextColor(itemView.getContext().getResources().getColor(R.color.optionDisable));
        }

        private boolean isValidUrl(String url) {
            try {
                new URL(url).toURI();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean isTex(String str) {
            if (str.contains("\\(") || str.contains("\\)") || str.contains("$") || str.contains("\\begin") || str.contains("\\end") || str.contains("\\ (") || str.contains("\\ )")) {
                return true;
            } else
                return false;

        }
    }

    public interface CheckResult {
        void onRadioCheck(int position, String yourAns, int ansPosition);
    }

}

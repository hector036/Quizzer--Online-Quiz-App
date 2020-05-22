package com.khan.quizzer_onlinequizapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import katex.hourglass.in.mathlib.MathView;

public class BookmarksAdater extends RecyclerView.Adapter<BookmarksAdater.Viewholder> {

    private static final int ANSSHEET = 1;
    private static final int BOOKMARK = 2;

    private List<QuestionModel> list;
    private int type;

    public BookmarksAdater(List<QuestionModel> list, int type) {
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmarks_item, parent, false);


        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.setData(list.get(position).getQuestion(), list.get(position).getUrl(), list.get(position).getCorrectAns(), list.get(position).getYourAns(), list.get(position).getCorrectAns(), position);
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

        private TextView answer, evaluation, yourAnswer;
        private ImageView figure;
        private MathView question;
        private TextView quesText;
        private ImageButton deleteBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.question);
            quesText = itemView.findViewById(R.id.quesText);
            figure = itemView.findViewById(R.id.figure_in_bookmarks);
            evaluation = itemView.findViewById(R.id.evaluation);
            answer = itemView.findViewById(R.id.answer);
            yourAnswer = itemView.findViewById(R.id.your_answer);
            deleteBtn = itemView.findViewById(R.id.delete_btn);


        }

        private void setData(String question, String url, String answer, String yourAns, String correctAns, final int position) {

            if (isTex(question)) {
                this.quesText.setVisibility(View.GONE);
                this.question.setVisibility(View.VISIBLE);
                if (type == BOOKMARK) {
                    this.question.setDisplayText(position + 1 + question);
                } else {
                    this.question.setDisplayText(question);
                }
            } else {
                this.question.setVisibility(View.GONE);
                this.quesText.setVisibility(View.VISIBLE);
                if (type == BOOKMARK) {
                    this.quesText.setText(position + 1 + question);
                } else {
                    this.quesText.setText(question);
                }
            }
            if (url.isEmpty()) {
                figure.setVisibility(View.GONE);
            } else {
                figure.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext()).load(url).placeholder(R.drawable.profile_edit).into(figure);
            }
            this.answer.setText("Correct Ans : " + answer);
            this.yourAnswer.setText("Your Ans : " + yourAns);

            if (type == ANSSHEET) {
                evaluation.setVisibility(View.VISIBLE);
                deleteBtn.setEnabled(false);
                deleteBtn.setVisibility(View.GONE);

                if (list.get(position).getYourAns() == null) {
                    yourAnswer.setVisibility(View.GONE);
                    evaluation.setText("Blank");
                    //evaluation.setBackgroundColor(Color.parseColor("#50000000"));
                    //evaluation.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#50000000")));
                    evaluation.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.evaluationBgTint_blank)));
                } else if (yourAns.equals(correctAns)) {
                    yourAnswer.setVisibility(View.GONE);
                    evaluation.setText("\u2714" + "  Correct");
                    //   evaluation.setBackgroundColor(Color.parseColor("#32CD32"));
                    evaluation.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1ABC1A")));

                } else {
                    yourAnswer.setVisibility(View.VISIBLE);
                    evaluation.setText("\u2718" + "  Wrong");
                    // evaluation.setBackgroundColor(Color.parseColor("#FA0000"));
                    evaluation.setBackgroundTintList(ColorStateList.valueOf(Color.RED));

                }

            } else if (type == BOOKMARK) {
                yourAnswer.setVisibility(View.GONE);
                evaluation.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.VISIBLE);
                deleteBtn.setEnabled(true);
            } else {

                yourAnswer.setVisibility(View.GONE);
                evaluation.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.GONE);
            }

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    // notifyItemRemoved(position);
                    notifyDataSetChanged();
                }
            });
        }

        private boolean isTex(String str) {
            if (str.contains("\\(") || str.contains("\\)") || str.contains("$") || str.contains("\\begin") || str.contains("\\end") || str.contains("\\ (") || str.contains("\\ )")) {
                return true;
            } else
                return false;

        }

    }
}

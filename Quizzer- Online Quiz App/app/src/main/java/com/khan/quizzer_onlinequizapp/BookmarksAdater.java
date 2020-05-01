package com.khan.quizzer_onlinequizapp;

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

public class BookmarksAdater extends RecyclerView.Adapter<BookmarksAdater.Viewholder>{


    private List<QuestionModel> list ;
    private int type;

    public BookmarksAdater(List<QuestionModel> list,int type) {
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmarks_item,parent,false);


        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.setData(list.get(position).getQuestion(),list.get(position).getUrl(),list.get(position).getCorrectAns(),list.get(position).getYourAns(),list.get(position).getCorrectAns(),position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{

        private TextView answer,evaluation,yourAnswer;
        private ImageView figure;
        private MathView question;
        private ImageButton deleteBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.question);
            figure = itemView.findViewById(R.id.figure_in_bookmarks);
            evaluation = itemView.findViewById(R.id.evaluation);
            answer = itemView.findViewById(R.id.answer);
            yourAnswer = itemView.findViewById(R.id.your_answer);
            deleteBtn = itemView.findViewById(R.id.delete_btn);


        }

        private void setData(String question,String url, String answer,String yourAns, String correctAns, final int position){

            this.question.setDisplayText(question);
            if(url.isEmpty()){
                figure.setVisibility(View.GONE);
            }else {
                figure.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext()).load(url).placeholder(R.drawable.profile_edit).into(figure);
            }
            this.answer.setText("Correct Ans : "+answer);
            this.yourAnswer.setText("Your Ans : "+yourAns);

            if(type==1){
                evaluation.setVisibility(View.VISIBLE);
                deleteBtn.setEnabled(false);
                deleteBtn.setVisibility(View.GONE);

                if(list.get(position).getYourAns()==null){
                    yourAnswer.setVisibility(View.GONE);
                    evaluation.setText("Blank");
                    evaluation.setBackgroundColor(Color.parseColor("#50000000"));
                }
                else if(yourAns.equals(correctAns)){
                    yourAnswer.setVisibility(View.GONE);
                    evaluation.setText("Correct");
                    evaluation.setBackgroundColor(Color.parseColor("#32CD32"));
                }
                else {
                    yourAnswer.setVisibility(View.VISIBLE);
                    evaluation.setText("Wrong");
                    evaluation.setBackgroundColor(Color.parseColor("#FA0000"));

                }

            }else if(type==2) {
                yourAnswer.setVisibility(View.GONE);
                evaluation.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.VISIBLE);
                deleteBtn.setEnabled(true);
            }else {

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

    }
}

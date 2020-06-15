package com.khan.quizzer_onlinequizapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.khan.quizzer_onlinequizapp.CategoriesActivity.FROM_SUBJECT_WISE_ACTIVITY;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.Viewholder> {

    private List<TestClass> chapters;
    private String category;
    private int type;

    public ChapterAdapter(int type, List<TestClass> chapters, String category) {
        this.chapters = chapters;
        this.category = category;
        this.type = type;
    }

    @NonNull
    @Override


    public ChapterAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_item, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterAdapter.Viewholder holder, int position) {
        String name = chapters.get(position).getSetName();
        String setId = chapters.get(position).getSetId();
        double socreInc = chapters.get(position).getSocreInc();
        double socreDe = chapters.get(position).getSocreDe();
        long time = chapters.get(position).getTime();
        String mcqUrl = chapters.get(position).getMcqUrl();
        String cqUrl = chapters.get(position).getCqUrl();
        holder.setData(name, setId, socreInc, socreDe, time, mcqUrl, cqUrl, position);

    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView title;
        private LinearLayout linearLayout;
        private Button mcqTest, viewSolution;
        private ImageView forword;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            linearLayout = itemView.findViewById(R.id.chapter_linearLayout);
            mcqTest = itemView.findViewById(R.id.mcq_test_chapter_item);
            viewSolution = itemView.findViewById(R.id.view_solution_chapter_item);
            forword = itemView.findViewById(R.id.forword_chapter);
        }

        private void setData(final String title, final String setId, final double socreInc, final double socreDe, final long time, String mcqUrl, String cqUrl, final int position) {
            this.title.setText(title);
            if ((mcqUrl == null || mcqUrl.equals("")) && (cqUrl == null || cqUrl.equals(""))) {
                linearLayout.setVisibility(View.GONE);
                forword.setVisibility(View.VISIBLE);
                this.title.setTextSize(15);
                this.title.setTypeface(this.title.getTypeface(), Typeface.NORMAL);
            }else {
                linearLayout.setVisibility(View.VISIBLE);
                forword.setVisibility(View.GONE);
                this.title.setTextSize(18);
                this.title.setTypeface(this.title.getTypeface(), Typeface.BOLD);
            }

            mcqTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent questionIntent = new Intent(itemView.getContext(), QuesbankQuestionsActivity.class);
                    questionIntent.putExtra("category", category);
                    questionIntent.putExtra("setId", setId);
                    questionIntent.putExtra("scoreInc", socreInc);
                    questionIntent.putExtra("scoreDe", socreDe);
                    questionIntent.putExtra("time", time);
                    itemView.getContext().startActivity(questionIntent);
                }
            });

            viewSolution.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewSolution = new Intent(itemView.getContext(), ViewSolutionActivity.class);
                    viewSolution.putExtra("category", category);
                    viewSolution.putExtra("setId", setId);
                    itemView.getContext().startActivity(viewSolution);

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == FROM_SUBJECT_WISE_ACTIVITY) {
                        Intent questionIntent = new Intent(itemView.getContext(), QuestionsActivity.class);
                        questionIntent.putExtra("category", category);
                        questionIntent.putExtra("setId", setId);
                        questionIntent.putExtra("type", 1);
                        itemView.getContext().startActivity(questionIntent);
                    }
                }
            });

        }
    }
}

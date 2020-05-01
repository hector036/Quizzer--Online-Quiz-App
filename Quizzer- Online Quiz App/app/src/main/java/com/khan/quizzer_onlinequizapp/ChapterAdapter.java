package com.khan.quizzer_onlinequizapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.Viewholder> {

    private List<TestClass> chapters;
    private String category;

    public ChapterAdapter(List<TestClass> chapters, String category) {
        this.chapters = chapters;
        this.category = category;
    }

    @NonNull
    @Override


    public ChapterAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_item,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterAdapter.Viewholder holder, int position) {
        holder.setData(chapters.get(position).getSetName(),chapters.get(position).getSetId(),position);

    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private TextView title;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);
        }

        private void setData( final String title,final String setId,final int position){
            //Glide.with(itemView.getContext()).load(url).into(imageView);
            this.title.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent questionIntent = new Intent( itemView.getContext(),QuestionsActivity.class);
                    questionIntent.putExtra("category",category);
                    questionIntent.putExtra("setId",setId);
                    questionIntent.putExtra("type",1);


                    itemView.getContext().startActivity(questionIntent);
                }
            });

        }
    }
}

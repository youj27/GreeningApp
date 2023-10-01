package com.example.greeningapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder>{
    private ArrayList<Review> dataList;
    private Context context;

    public ReviewAdapter(ArrayList<Review> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(holder.itemView).load(dataList.get(position).getRimage()).into(holder.recyclerImage);
        holder.recyclerEt.setText(String.valueOf(dataList.get(position).getRcontent()));
        holder.reviewdate.setText(String.valueOf(dataList.get(position).getRdatetime()));
        holder.recyclerRatingbar.setRating(dataList.get(position).getRscore());
    }

    @Override
    public int getItemCount() {
        return (dataList!=null ? dataList.size() :0);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView recyclerImage;
        private TextView recyclerEt;
        private RatingBar recyclerRatingbar;
        private TextView reviewdate;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.recyclerImage = itemView.findViewById(R.id.fullreviewImage);
            this.recyclerEt = itemView.findViewById(R.id.fullreviewEt);
            this.recyclerRatingbar = itemView.findViewById(R.id.fullreviewRatingBar);
            this.reviewdate = itemView.findViewById(R.id.fullreviewDate);
        }
    }
}
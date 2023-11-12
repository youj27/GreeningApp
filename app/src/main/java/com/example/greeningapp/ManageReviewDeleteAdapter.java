package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class ManageReviewDeleteAdapter extends RecyclerView.Adapter<ManageReviewDeleteAdapter.ManageReviewDeleteViewHolder> {
    private ArrayList<Review> reviewdeleteList;
    private Context context;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;


    public ManageReviewDeleteAdapter(ArrayList<Review> arrayList, Context context){
        this.reviewdeleteList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ManageReviewDeleteAdapter.ManageReviewDeleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_review_similar_list, parent, false);
        ManageReviewDeleteAdapter.ManageReviewDeleteViewHolder holder = new ManageReviewDeleteAdapter.ManageReviewDeleteViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ManageReviewDeleteAdapter.ManageReviewDeleteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(holder.itemView).load(reviewdeleteList.get(position).getRimage()).into(holder.MGReviewInputimg_review);
        holder.MGOrderID_order.setText(reviewdeleteList.get(position).getReviewid());
        holder.MGReviewDate_review.setText(reviewdeleteList.get(position).getRdatetime());
        holder.MGReviewUsername_review.setText(reviewdeleteList.get(position).getUsername());
        holder.MGReviewReviewdes_review.setText(reviewdeleteList.get(position).getRcontent());

        databaseReference = FirebaseDatabase.getInstance().getReference("Review");

        holder.MGReviewUserrating_review.setRating(reviewdeleteList.get(position).getRscore());

        // 여기에서 유사도를 계산
        double similarity = calculateSimilarity(reviewdeleteList.get(position));
        //float similarity = calculateSimilarity(reviewdeleteList.get(position));
        // 유사도를 텍스트뷰에 표시
        holder.MGReview_Similarcontent.setText("유사도: " + similarity);

//        holder.MGSimilarReview_review.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, ManageReviewSimilarityActivity.class);
//                intent.putExtra("ManageReviewDelete", (Serializable) reviewdeleteList.get(position));
//                context.startActivity(intent);
//
//            }
//        });
        
    }

//    // 유사도를 계산하는 메서드
//    private double calculateSimilarity(Review review) {
//        // 실제 유사도를 계산하는 로직을 작성
//        // 여기에서는 더미 값 사용
//        // 코랩에서 사용한 로직을 참고하여 실제 유사도 계산을 수행
//        // 이 부분은 모델을 활용하여 텍스트 유사도를 계산하는 코드로 대체되어야 합니다.
//
//        // 예시로 코랩에서 사용한 preprocessInput 메서드를 활용하여 입력 데이터를 전처리하고 모델에 적용
//        float[][] input = preprocessInput(review.getRcontent());
//
//        // 여기에서는 더미 값 사용
//        float[][] dummyOutput = {{0.75}};
//
//        // 실제 모델을 활용하여 유사도 계산
//        // 모델을 사용하는 방법은 모델의 로드와 추론을 포함합니다.
//        // 모델을 불러오고 추론하는 방법은 사용하는 프레임워크(Keras, TensorFlow) 및 모델 형식에 따라 다를 수 있습니다.
//        // 아래 코드는 추론 결과를 dummyOutput에서 가져오는 예시 코드입니다.
//        double similarity = dummyOutput[0][0];
//
//        return similarity;
//    }
//
//    // 전처리 메서드 예시
//    private float[][] preprocessInput(String reviewContent) {
//        // 실제 전처리 로직을 작성
//        // 여기에서는 더미 값 사용
//        float[][] dummyInput = {{0.5f, 0.3f, 0.8f, 0.2f}};
//        return dummyInput;
//    }


    @Override
    public int getItemCount() {
        if (reviewdeleteList != null) {
            return reviewdeleteList.size();
        }
        return 0;
    }

    public class ManageReviewDeleteViewHolder extends RecyclerView.ViewHolder {
        ImageView MGReviewInputimg_review;
        TextView MGOrderID_order, MGReviewDate_review, MGReviewUsername_review, MGReviewReviewdes_review;
        RatingBar MGReviewUserrating_review;

        TextView MGReview_Similarcontent;

        Button MGSimilarReview_review;

        public ManageReviewDeleteViewHolder(@NonNull View itemView) {
            super(itemView);

            this.MGReviewInputimg_review = itemView.findViewById(R.id.MGReviewInputimg_review);
            this.MGOrderID_order = itemView.findViewById(R.id.MGOrderID_order);
            this.MGReviewDate_review = itemView.findViewById(R.id.MGReviewDate_review);
            this.MGReviewUsername_review = itemView.findViewById(R.id.MGReviewUsername_review);
            this.MGReviewReviewdes_review = itemView.findViewById(R.id.MGReviewReviewdes_review);
            this.MGReviewUserrating_review = itemView.findViewById(R.id.MGReviewUserrating_review);

            this.MGSimilarReview_review = itemView.findViewById(R.id.MGSimilarReview_review);

            this.MGReview_Similarcontent = itemView.findViewById(R.id.MGReview_Similarcontent);
        }
    }

    //잠시 주석
    // 유사도를 계산하는 메서드
    private double calculateSimilarity(Review review) {
        String reviewContent = review.getRcontent();
        TextSimilarityClassifier classifier;
        try {
            classifier = new TextSimilarityClassifier(context);
            float[][] input = preprocessInput(reviewContent);
            float similarity = classifier.predictSimilarity(input);
            classifier.close();
            return similarity;
        } catch (IOException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

//    //추가
//    private float calculateSimilarity(Review review) {
//        String reviewContent = review.getRcontent();
//        SimilarityCalculator calculator;
//        try {
//            calculator = new SimilarityCalculator(context);
//            float similarity = calculator.calculateSimilarity("좋아요", reviewContent);
//            // "좋아요"는 비교 기준이므로 적절한 비교 기준을 사용해야 함
//            calculator.close();
//            return similarity;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return 0.0f;
//        }
//    }


    // 텍스트를 모델 입력 형식으로 전처리하는 메서드
    private float[][] preprocessInput(String text) {
        // 실제 전처리 로직을 여기에 추가
        // 여기에서는 코랩 코드를 참고하여 더미 값 사용

        float[][] dummyInput = new float[1][4];
        dummyInput[0][0] = 0.5f;
        dummyInput[0][1] = 0.3f;
        dummyInput[0][2] = 0.8f;
        dummyInput[0][3] = 0.2f;

        return dummyInput;

//        // 적절한 전처리 로직을 적용하여 텍스트를 모델 입력 형태로 변환
//        // 여기에서는 Tokenizer 등을 활용하여 텍스트를 임베딩으로 변환하는 것이 적절
//
//        // 예시로 단어 개수를 세고 각 단어를 임베딩하는 코드
//        String[] words = text.split("\\s+");
//        float[][] input = new float[1][words.length * EMBEDDING_SIZE];
//
//        // 실제로는 각 단어를 임베딩하여 input 배열에 할당하는 로직이 들어감
//
//        return input;
    }
}

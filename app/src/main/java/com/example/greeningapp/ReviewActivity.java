package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<ReviewData> dataList;
    private ReviewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        recyclerView = findViewById(R.id.fullrecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        layoutManager = new LinearLayoutManager(this);
        dataList = new ArrayList<>();

        database = FirebaseDatabase.getInstance(); //파이어베이스 연동

        databaseReference = database.getReference("Review");//db데이터연결

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear(); //기준 배열리스트가 존재하지않게 초기화(데이터가 쌓이기때문)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터리스트 추출
                    ReviewData review = snapshot.getValue(ReviewData.class);  //만들어뒀던 review객체에 데이터를 담는다( 리뷰작성시 )
                    dataList.add(review); //담은 데이터들을 배열리스트에 넣고 리사이클뷰로 보낼준비
                }
                adapter.notifyDataSetChanged();
                //db가져오던중 에러발생시
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); //에러문출력
            }
        });
        adapter = new ReviewAdapter(dataList, this);
        recyclerView.setAdapter(adapter);  //리사이클뷰에 어댑터연결

        FirebaseDatabase mRef = FirebaseDatabase.getInstance();
        DatabaseReference ratingsRef = mRef.getReference("Reviews");



        ratingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float totalRating = 0;
                int ratingCount = 0;

                for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                    float rating = ratingSnapshot.child("Rating").getValue(Float.class);
                    totalRating += rating;
                    ratingCount++;
                }

                float averageRating = 0;
                if (ratingCount != 0) {
                    averageRating = totalRating / ratingCount;
                }
                String formattedRating = String.format("%.2f", averageRating);

                TextView reviewRating = findViewById(R.id.reviewValue);
                reviewRating.setText(formattedRating);

                // 계산된 평점 값을 레이팅바에 표시
                RatingBar ratingBar = findViewById(R.id.reviewRating);
                float scaledRating = Math.round(averageRating * 5 / 5.0f);  // 평점 값을 5로 스케일링하고 소수점 자리 반올림
                ratingBar.setRating(scaledRating);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 처리 오류가 발생한 경우에 대한 예외 처리를 수행할 수 있습니다.
            }
        });
    }
}
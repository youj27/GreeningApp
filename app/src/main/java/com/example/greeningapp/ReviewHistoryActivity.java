//package com.example.greeningapp;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ReviewHistoryActivity extends AppCompatActivity {
//
//    FirebaseDatabase firebaseDatabase;
//    DatabaseReference databaseReference2;
//    DatabaseReference databaseReferenceProduct;
//
//    private FirebaseAuth firebaseAuth;
//    private DatabaseReference databaseReference;
//
//    FirebaseDatabase database;
//
//    ReviewData product = null;
//    TextView Pname;
//    ImageView Pimg;
//
//    //private RecyclerView parentRecyclerView;
//
//    //private ArrayList<MyOrder> parentModelArrayList;
//    //private RecyclerView.Adapter ParentAdapter;
//
//    //private RecyclerView.LayoutManager parentLayoutManager;
//
//    private RecyclerView recyclerView;
//    private ArrayList<ReviewData> dataList;
//    private List<MyOrder> MyOrderPList = new ArrayList<>();   //잠시 추가
//
//    private ReviewHistoryAdapter adapter;
//    private RecyclerView.LayoutManager layoutManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_review_history);
//
//        recyclerView = findViewById(R.id.reviewHisyoryRecycler);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        layoutManager = new LinearLayoutManager(this);
//        dataList = new ArrayList<>();
//
//        database = FirebaseDatabase.getInstance(); //파이어베이스 연동
//        databaseReference = database.getReference("Review");//db데이터연결
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            //@SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                dataList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    ReviewData review = snapshot.getValue(ReviewData.class);
//                    dataList.add(review);
//
//                }
//                adapter.notifyDataSetChanged();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("ReviewHistoryActivity", String.valueOf(databaseError.toException()));
//            }
//        });
//        adapter = new ReviewHistoryAdapter(dataList, this); //MyOrderPList 잠시 추가
//        recyclerView.setAdapter(adapter);
//
//    }
//}


//////////
package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewHistoryActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    FirebaseDatabase database;
    private RecyclerView recyclerView;
    private ArrayList<Review> reviewhistoryList;
    private ReviewHistoryAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private DatabaseReference databaseReference2;

    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_history);

        recyclerView = findViewById(R.id.reviewHisyoryRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        layoutManager = new LinearLayoutManager(this);
        reviewhistoryList = new ArrayList<>();

        database = FirebaseDatabase.getInstance(); //파이어베이스 연동
        //firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = database.getReference("Review"); // Firebase Realtime Database에서 "Review" 항목을 가져옵니다.

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        databaseReference2 = FirebaseDatabase.getInstance().getReference("User");

        databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    username = user.getUsername();

                    Query reviewhistoryQuery = databaseReference.orderByChild("username").equalTo(username);
                    reviewhistoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            reviewhistoryList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Review review = snapshot.getValue(Review.class);
                                reviewhistoryList.add(review);
                                Log.d("usename", review.getUsername() + "가져왔음");
                            }
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("ReviewHistoryActivity", String.valueOf(databaseError.toException()));
                        }
                    });
                    adapter = new ReviewHistoryAdapter(reviewhistoryList, ReviewHistoryActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}



//        Query reviewhistoryQuery = databaseReference2.orderByChild("username").equalTo("uz");
//
//        reviewhistoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                reviewhistoryList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Review review = snapshot.getValue(Review.class);
//                    reviewhistoryList.add(review);
//                    Log.d("usename",review.getUsername() +"가져왔음");
//
//                }
//                adapter.notifyDataSetChanged();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("ReviewHistoryActivity", String.valueOf(databaseError.toException()));
//            }
//        });
//        adapter = new ReviewHistoryAdapter(reviewhistoryList, this);
//        recyclerView.setAdapter(adapter);
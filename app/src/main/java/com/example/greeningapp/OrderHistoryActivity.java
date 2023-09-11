package com.example.greeningapp;

import android.os.Bundle;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderHistoryActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private RecyclerView parentRecyclerView;

    private ArrayList<MyOrder> parentModelArrayList;
    private RecyclerView.Adapter ParentAdapter;

    private RecyclerView.LayoutManager parentLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser").child(firebaseUser.getUid()).child("MyOrder");

        parentRecyclerView = findViewById(R.id.Parent_recyclerView);
        parentRecyclerView.setHasFixedSize(true);
        parentLayoutManager = new LinearLayoutManager(this);
        parentRecyclerView.setLayoutManager(parentLayoutManager);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parentModelArrayList = new ArrayList<>();
                parentModelArrayList.clear();

                for (DataSnapshot parentSnapshot : dataSnapshot.getChildren()) {
                    ArrayList<MyOrder> childModelArrayList = new ArrayList<>();

                    for (DataSnapshot childSnapshot : parentSnapshot.getChildren()) {
                        MyOrder childOrder = childSnapshot.getValue(MyOrder.class);
                        childModelArrayList.add(childOrder);
                    }

                    if (!childModelArrayList.isEmpty()) {
                        MyOrder parentOrder = childModelArrayList.get(0);
                        parentOrder.setChildModelArrayList(childModelArrayList);
                        parentModelArrayList.add(parentOrder);
                    }
                }

                ParentAdapter = new OrderHistoryParentActivity(parentModelArrayList, OrderHistoryActivity.this);
                parentRecyclerView.setAdapter(ParentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("OrderHistoryActivity", String.valueOf(databaseError.toException()));
            }
        });



    }
}
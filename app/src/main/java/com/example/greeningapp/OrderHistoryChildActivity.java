//package com.example.greeningapp;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.widget.AppCompatButton;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//
//import java.util.ArrayList;
//
//public class OrderHistoryChildActivity extends RecyclerView.Adapter<OrderHistoryChildActivity.ChildViewHolder> {
//    public ArrayList<MyOrder> childModelArrayList;
//    Context cxt;
//    private String isReviewCompleted = "";
//
//
//
//
//
//    public OrderHistoryChildActivity(ArrayList<MyOrder> childModelArrayList, Context mContext) {
//        this.cxt = mContext;
//        this.childModelArrayList = childModelArrayList;
//
//
//    }
//
//    @Override
//    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_order_history_child, parent, false);
//        return new ChildViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ChildViewHolder holder, @SuppressLint("RecyclerView") int position) {
//
////        OrderHistoryChild currentItem = childModelArrayList.get(position);
////        MyOrder myItem = childModelArrayList.get(position);  //잠시추가
//
//        Glide.with(holder.itemView)
//                .load(childModelArrayList.get(position).getOrderImg())
//                .into(holder.orderhistory_img);
//        holder.pro_name.setText(childModelArrayList.get(position).getProductName());
//        holder.pro_price.setText(childModelArrayList.get(position).getProductPrice());
//        holder.ordervalue.setText(childModelArrayList.get(position).getTotalQuantity() + "개");
//
//        isReviewCompleted = childModelArrayList.get(position).getDoReview();
//
//        if("No".equals(isReviewCompleted)){
//
//            holder.ordhreviewBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent intent = new Intent(cxt, ReviewWriteActivity.class);
//                    intent.putExtra("product", childModelArrayList.get(position));
//                    Log.d("Order", String.valueOf(childModelArrayList.get(position)+"가져왔음"));
//                    cxt.startActivity(intent);
//
//                    isReviewCompleted = "Yes"; // 리뷰 작성이 완료되었다고 설정
//                    holder.ordhreviewBtn.setText("작성 완료"); // 버튼 텍스트를 "작성 완료"로 설정
//                    holder.ordhreviewBtn.setEnabled(false); // 버튼을 비활성화
//
//                }
//            });
//        } else if("Yes".equals(isReviewCompleted)){
//            holder.ordhreviewBtn.setText("작성 완료");
//            holder.ordhreviewBtn.setEnabled(false);
//        }
//
//    }
//
//    @Override
//    public int getItemCount() {
//        if (childModelArrayList != null) {
//            return childModelArrayList.size();
//        }
//        return 0;
//    }
//
//    public class ChildViewHolder extends RecyclerView.ViewHolder {
//        public ImageView orderhistory_img;
//        public TextView pro_name, pro_price, ordervalue;
//
//        AppCompatButton ordhreviewBtn;
//
//        public ChildViewHolder(View itemView) {
//            super(itemView);
//            orderhistory_img = itemView.findViewById(R.id.orderhistory_img);
//            pro_name = itemView.findViewById(R.id.pro_name);
//            pro_price = itemView.findViewById(R.id.pro_price);
//            ordervalue = itemView.findViewById(R.id.ordervalue);
//            ordhreviewBtn = itemView.findViewById(R.id.ordhreviewBtn);
//        }
//    }
//}
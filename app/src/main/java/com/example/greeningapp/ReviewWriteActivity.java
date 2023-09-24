package com.example.greeningapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ReviewWriteActivity extends AppCompatActivity {

    private static final int Gallery_Code=1;

    FirebaseDatabase mDatabase;
    DatabaseReference mRef;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    FirebaseStorage mStorage;

    ImageView uploadImage;
    Button uploadBtn;
    RatingBar RatingBarEt;
    Uri imageUri=null;
    ImageButton cancelBtn;
    EditText reviewEt;

    MyOrder product = null;

    TextView Pname;
    ImageView Pimg;


    TextView mDate;  //날짜



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser").child(firebaseUser.getUid()).child("MyOrder");

        uploadBtn = findViewById(R.id.writeUploadBtn);
        //Button uploadBtn = findViewById(R.id.writeUploadBtn);
        uploadImage = findViewById(R.id.writeUploadImage);
        reviewEt = findViewById(R.id.writeReviewEt);
        cancelBtn = findViewById(R.id.writeCancelBtn);
        RatingBarEt = findViewById(R.id.writeRatingBar);

        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference().child("Review");
        mStorage=FirebaseStorage.getInstance();
        //날짜 표시
        mDate = findViewById(R.id.reviewDate);

        String dateTimeFormat = "yyyy.MM.dd";
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimeFormat);
        String formattedDate = simpleDateFormat.format(date);
        mDate.setText(formattedDate);

        Pname = findViewById(R.id.writePname);
        Pimg = (ImageView) findViewById(R.id.writePImg);

        final Object object = getIntent().getSerializableExtra("product");

        if(object instanceof MyOrder){
            product = (MyOrder) object;
            Log.d("ReviewWriteActivity", product+"");
        }

        if (product != null) {
            Pname.setText(product.getProductName());
            Glide.with(getApplicationContext()).load(product.getOrderImg()).into(Pimg);

        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewWriteActivity.this, ReviewActivity.class);
                startActivity(intent);
            }
        });


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                startActivityForResult(intent,Gallery_Code);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_Code && resultCode == RESULT_OK)
        {
            imageUri =data.getData();
            uploadImage.setImageURI(imageUri);
        }

        Button uploadBtn = findViewById(R.id.writeUploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fn = reviewEt.getText().toString().trim();
                //String reviewImage = imageUri.toString();
                String reviewImage = (imageUri != null) ? imageUri.toString() : ""; // 이미지 URI가 null이 아닌지 확인

                if (!fn.isEmpty())  //후기작성 내용이 비어있지않으면 업로드 진행
                {
                    float rating = RatingBarEt.getRating();
                    String reviewDate = mDate.getText().toString();

                    //String pname = Pname.getText().toString();
                    //String pimg = product.getOrderImg();
//                    String pname = product.getProductName(); // 제품 이름을 product 객체에서 가져옵니다.
//                    String pimg = product.getOrderImg();

                    // Create a HashMap to store the review data
                    HashMap<String, Object> reviewwriteMap = new HashMap<>();
                    reviewwriteMap.put("pid", product.getProductId());
                    reviewwriteMap.put("pname", product.getProductName());
                    reviewwriteMap.put("pimg", product.getOrderImg());
                    reviewwriteMap.put("username", product.getUserName());
                    reviewwriteMap.put("Review_image", reviewImage);
                    reviewwriteMap.put("Write_review", fn);
                    reviewwriteMap.put("Rating", rating);
                    reviewwriteMap.put("Review_date", reviewDate);

//                    mRef.push().setValue(reviewwriteMap).addOnSuccessListener(aVoid -> {
//                        product.setDoReview(true);
//                    }).addOnFailureListener(e -> {
//
//                    });
                    Log.d("Review", "리뷰 작성 여부 " +  product.getDoReview());

                    mRef.push().setValue(reviewwriteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Firebase 데이터 쓰기가 성공한 경우
                                product.setDoReview("Yes");

                            } else {
                                // Firebase 데이터 쓰기가 실패한 경우
                                Log.e("Firebase", "Data write failed: " + task.getException().getMessage());
                            }
                        }
                    });


                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewWriteActivity.this);

                    builder.setTitle("작성 완료").setMessage("감사합니다!");


                    builder.setPositiveButton("홈 이동", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(ReviewWriteActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });

                    builder.setNegativeButton("시드 확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(ReviewWriteActivity.this, ReviewHistoryActivity.class);
                            startActivity(intent);
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }else {

                }
            }

        });
    }
}


    //<이전 코드(product 추가 전)>
//package com.example.greeningapp;
//
//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.Activity;
//import android.content.ContentResolver;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.webkit.MimeTypeMap;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.RatingBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.OnProgressListener;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//
//public class ReviewWriteActivity extends AppCompatActivity {
//
//    private static final int Gallery_Code=1;
//
//    FirebaseDatabase mDatabase;
//    DatabaseReference mRef;
//    FirebaseStorage mStorage;
//
//    ImageView uploadImage;
//    Button uploadBtn;
//    RatingBar RatingBarEt;
//    Uri imageUri=null;
//    Button cancelBtn;
//    EditText reviewEt;
//
//
//    TextView mDate;  //날짜
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_review_write);
//
//
//        uploadBtn = findViewById(R.id.writeUploadBtn);
//        uploadImage = findViewById(R.id.writeUploadImage);
//        reviewEt = findViewById(R.id.writeReviewEt);
//        cancelBtn = findViewById(R.id.writeCancelBtn);
//        RatingBarEt = findViewById(R.id.writeRatingBar);
//
//        mDatabase=FirebaseDatabase.getInstance();
//        mRef=mDatabase.getReference().child("Review");
//        mStorage=FirebaseStorage.getInstance();
//        //날짜 표시
//        mDate = findViewById(R.id.reviewDate);
//
//        String dateTimeFormat = "yyyy.MM.dd";
//        Date date = Calendar.getInstance().getTime();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimeFormat);
//        String formattedDate = simpleDateFormat.format(date);
//        mDate.setText(formattedDate);
//
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ReviewWriteActivity.this, ReviewActivity.class);
//                startActivity(intent);
//            }
//        });
//
//
//        uploadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/");
//                startActivityForResult(intent,Gallery_Code);
//            }
//        });
//
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode==Gallery_Code && resultCode == RESULT_OK)
//        {
//            imageUri =data.getData();
//            uploadImage.setImageURI(imageUri);
//        }
//
//        uploadBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String fn = reviewEt.getText().toString().trim();
//                String reviewImage = imageUri.toString();
//
//                if (!(fn.isEmpty() && imageUri != null))   //이미지와 후기작성이 비어있지않으면 업로드 진행
//                {
//                    float rating = RatingBarEt.getRating();
//                    String reviewDate = mDate.getText().toString();
//
//                    // Create a HashMap to store the review data
//                    HashMap<String, Object> reviewwriteMap = new HashMap<>();
//                    reviewwriteMap.put("Review_image", reviewImage);
//                    reviewwriteMap.put("Write_review", fn);
//                    reviewwriteMap.put("Rating", rating);
//                    reviewwriteMap.put("Review_date", reviewDate);
//
//                    mRef.push().setValue(reviewwriteMap).addOnSuccessListener(aVoid -> {
//
//                    }).addOnFailureListener(e -> {
//
//                    });
//
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewWriteActivity.this);
//
//                    builder.setTitle("작성 완료").setMessage("감사합니다!");
//
//
//                    builder.setPositiveButton("홈 이동", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int id) {
//                            Intent intent = new Intent(ReviewWriteActivity.this, MainActivity.class);
//                            startActivity(intent);
//                        }
//                    });
//
//                    builder.setNegativeButton("시드 확인", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int id) {
//                            Intent intent = new Intent(ReviewWriteActivity.this, ReviewHistoryActivity.class);
//                            startActivity(intent);
//                        }
//                    });
//
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//
//                }else {
//
//                }
//            }
//
//        });
//    }
//}
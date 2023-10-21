package com.example.greeningapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ReviewWriteActivity extends AppCompatActivity {

    private static final int Gallery_Code=1;

    FirebaseDatabase mDatabase;
    DatabaseReference mRef;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private DatabaseReference databaseReference2;
    private DatabaseReference databaseReference3;
    //DatabaseReference databaseReference2; //잠시 추가 -> //MyOrder

    private BottomNavigationView bottomNavigationView;

    private ImageButton navMain, navCategory, navDonation, navMypage;

    FirebaseStorage mStorage;
    ImageView uploadImage;
    Button uploadBtn;
    RatingBar RatingBarEt;
    Uri imageUri=null;
    //ImageButton cancelBtn;
    EditText reviewEt;
    MyOrder product = null;
    TextView Pname;
    //TextView Pprice;
    ImageView Pimg;
    TextView mDate;  //날짜

    Dialog Reviewdialog;

    private String orderId,myOrderId, eachOrderedId;
    private int userSPoint;

    Toolbar rtoolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);



        rtoolbar = findViewById(R.id.toolbar_reviewwrite);
        setSupportActionBar(rtoolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목 삭제.
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser").child(firebaseUser.getUid()).child("MyOrder");

        uploadBtn = findViewById(R.id.writeUploadBtn);
        //Button uploadBtn = findViewById(R.id.writeUploadBtn);
        uploadImage = findViewById(R.id.writeUploadImage);
        reviewEt = findViewById(R.id.writeReviewEt);
        //cancelBtn = findViewById(R.id.writeCancelBtn);
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

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
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
                //String reviewImage = (imageUri != null) ? imageUri.toString() : null; // 이미지 URI가 null인지 확인하고 null인 경우 null로 처리

                if (!fn.isEmpty())  //후기작성 내용이 비어있지않으면 업로드 진행,  주석: if (!fn.isEmpty() || reviewImage == null || reviewImage != null)
                {
                    float rating = RatingBarEt.getRating();
                    String reviewDate = mDate.getText().toString();

                    String Pname = product.getProductName(); // 제품 이름을 String으로 저장
                    String Pimg = product.getOrderImg(); // 제품 이미지 URL을 String으로 저장

                    // Create a HashMap to store the review data
                    HashMap<String, Object> reviewwriteMap = new HashMap<>();
                    reviewwriteMap.put("pid", product.getProductId());
                    reviewwriteMap.put("pname", product.getProductName());
                    reviewwriteMap.put("pimg", product.getOrderImg());
                    reviewwriteMap.put("username", product.getUserName());
                    reviewwriteMap.put("pprice", product.getProductPrice());
                    reviewwriteMap.put("totalquantity", product.getTotalQuantity());
                    reviewwriteMap.put("rimage", reviewImage);
                    reviewwriteMap.put("rcontent", fn);
                    reviewwriteMap.put("rscore", rating);
                    reviewwriteMap.put("rdatetime", reviewDate);

                    Log.d("Review", "리뷰 작성 여부 " + product.getDoReview());

//                    int pidInt = product.getProductId(); // 정수 값을 가져온후
//                    String pid = String.valueOf(pidInt);  //문자열로 변환하여 저장


//                    mRef.push().setValue(reviewwriteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                // Firebase 데이터 쓰기가 성공한 경우
//                                product.setDoReview("Yes");
//
//                            } else {
//                                // Firebase 데이터 쓰기가 실패한 경우
//                                Log.e("Firebase", "Data write failed: " + task.getException().getMessage());
//                            }
//                        }
//                    });

                    //mRef.push().child(pid).setValue(reviewwriteMap).addOnCompleteListener
                    mRef.push().setValue(reviewwriteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                databaseReference.child(product.getOrderId()).child(product.getEachOrderedId()).child("doReview").setValue("Yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Myreview", "myOrderId: " + myOrderId);
                                        Log.d("eachorderid", "eachOrderedId: " + eachOrderedId);
                                    }
                                });

                            } else {
                                // Firebase 데이터 쓰기가 실패한 경우
                                Log.e("Firebase", "Data write failed: " + task.getException().getMessage());
                            }
                        }
                    });


                    //AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewWriteActivity.this);

                    builder.setTitle("작성 완료").setMessage("감사합니다!");


                    builder.setPositiveButton("홈 이동", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(ReviewWriteActivity.this, ReviewActivity.class);
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
                    // 리뷰 내용 또는 이미지가 비어 있는 경우에 대한 처리

                }

//                public void showReviewdialog() {
//                    Reviewdialog.show();
//
//                    Reviewdialog.show();
//
//                    TextView confirmTextView = Reviewdialog.findViewById(R.id.confirmTextView);
//                    confirmTextView.setText("작성이 완료되었습니다.");
//
//                    Button btnOk = Reviewdialog.findViewById(R.id.btn_ok);
//                    btnOk.setText("주문내역으로 돌아가기");
//                    btnOk.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Reviewdialog.dismiss();
//                        }
//                    });
//                }

            }

        });

        // 하단바 구현
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_ReviewWrite);
        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_mypage);

        // BottomNavigationView의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(ReviewWriteActivity.this, MainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(ReviewWriteActivity.this, CategoryActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(ReviewWriteActivity.this, DonationMainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(ReviewWriteActivity.this, MyPageActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) { //뒤로가기
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}



// 이전 uploadBtn 코드
//uploadBtn.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        String fn = reviewEt.getText().toString().trim();
//        //String reviewImage = imageUri.toString();
//        String reviewImage = (imageUri != null) ? imageUri.toString() : ""; // 이미지 URI가 null이 아닌지 확인
//
//        if (!fn.isEmpty())  //후기작성 내용이 비어있지않으면 업로드 진행
//        {
//        float rating = RatingBarEt.getRating();
//        String reviewDate = mDate.getText().toString();
//
//        String Pname = product.getProductName(); // 제품 이름을 String으로 저장
//        String Pimg = product.getOrderImg(); // 제품 이미지 URL을 String으로 저장
//
//        // Create a HashMap to store the review data
//        HashMap<String, Object> reviewwriteMap = new HashMap<>();
//        reviewwriteMap.put("pid", product.getProductId());
//        reviewwriteMap.put("pname", product.getProductName());
//        reviewwriteMap.put("pimg", product.getOrderImg());
//        reviewwriteMap.put("username", product.getUserName());
//        reviewwriteMap.put("rimage", reviewImage);
//        reviewwriteMap.put("rcontent", fn);
//        reviewwriteMap.put("rscore", rating);
//        reviewwriteMap.put("rdatetime", reviewDate);
//
//        Log.d("Review", "리뷰 작성 여부 " +  product.getDoReview());
//
//        mRef.push().setValue(reviewwriteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if (task.isSuccessful()) {
//        // Firebase 데이터 쓰기가 성공한 경우
//        product.setDoReview("Yes");
//
//        } else {
//        // Firebase 데이터 쓰기가 실패한 경우
//        Log.e("Firebase", "Data write failed: " + task.getException().getMessage());
//        }
//        }
//        });
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewWriteActivity.this);
//
//        builder.setTitle("작성 완료").setMessage("감사합니다!");
//
//
//        builder.setPositiveButton("홈 이동", new DialogInterface.OnClickListener() {
//@Override
//public void onClick(DialogInterface dialog, int id) {
//        Intent intent = new Intent(ReviewWriteActivity.this, ReviewActivity.class);
//        startActivity(intent);
//        }
//        });
//
//        builder.setNegativeButton("시드 확인", new DialogInterface.OnClickListener() {
//@Override
//public void onClick(DialogInterface dialog, int id) {
//        Intent intent = new Intent(ReviewWriteActivity.this, ReviewHistoryActivity.class);
//        startActivity(intent);
//        }
//        });
//
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//
//        }else {
//        //showErrorDialog("후기 내용을 입력하세요.");
//
//        }
//        }
//
//        });


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
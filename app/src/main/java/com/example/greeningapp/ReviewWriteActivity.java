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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ReviewWriteActivity extends AppCompatActivity {

    String fn;
    String reviewImage;

    private static final int Gallery_Code=1;
    FirebaseUser firebaseUser;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private DatabaseReference databaseReference3;

    private BottomNavigationView bottomNavigationView;

    private ImageButton navMain, navCategory, navDonation, navMypage;

    StorageReference storageReference;
    ImageView uploadImage;
    Button uploadBtn;
    RatingBar RatingBarEt;
    Uri imageUri=null;
    EditText reviewEt;
    MyOrder product = null;
    TextView Pname;
    ImageView Pimg;
    TextView mDate;

    Dialog Reviewdialog;

    private String orderId,myOrderId, eachOrderedId;
    private int userSPoint;

    Toolbar rtoolbar;
    Dialog dialog;
    Dialog dialog2;
    String reviewId;

    long mNow;
    Date mDate2;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);


        // 툴바 설정
        rtoolbar = findViewById(R.id.toolbar_reviewwrite);
        setSupportActionBar(rtoolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        // 다이얼로그 설정
        dialog = new Dialog(ReviewWriteActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm2);

        dialog2 = new Dialog(ReviewWriteActivity.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.dialog_confirm);

        // 파이어베이스 설정
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser").child(firebaseUser.getUid()).child("MyOrder");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("User");
        databaseReference3 = FirebaseDatabase.getInstance().getReference("CurrentUser");

        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference().child("Review");
        storageReference=FirebaseStorage.getInstance().getReference();
        mDate = findViewById(R.id.reviewDate);

        // 레이아웃 요소
        uploadBtn = findViewById(R.id.writeUploadBtn);
        uploadImage = findViewById(R.id.writeUploadImage);
        reviewEt = findViewById(R.id.writeReviewEt);
        RatingBarEt = findViewById(R.id.writeRatingBar);
        uploadBtn = findViewById(R.id.writeUploadBtn);

        Pname = findViewById(R.id.writePname);
        Pimg = (ImageView) findViewById(R.id.writePImg);


        final Object object = getIntent().getSerializableExtra("product"); // MyOrder 데이터 가져오기

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


        // 하단바 설정
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_ReviewWrite);
        bottomNavigationView.setSelectedItemId(R.id.tab_mypage);

        // 하단바 구현
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(ReviewWriteActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(ReviewWriteActivity.this, CategoryActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(ReviewWriteActivity.this, DonationMainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // MyPage 액티비티로 이동
                    startActivity(new Intent(ReviewWriteActivity.this, MyPageActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

        // 이미지 업로드 구현
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fn = reviewEt.getText().toString().trim();

                if (!fn.isEmpty()) {
                    uploadImagesAndSaveData(); // 이미지 업로드
                } else {
                    showReivewDialog();
                }

                // 구매후기 작성 시 포인트 적립
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        userSPoint = user.getSpoint();
                        double changePoint = userSPoint + 50; // 50 씨드 적립
                        databaseReference2.child(firebaseUser.getUid()).child("spoint").setValue(changePoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // 구매후기 작성 시 포인트 데이터 저장
                databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        final HashMap<String, Object> pointMap = new HashMap<>();
                        pointMap.put("pointName", "씨드 적립 - 구매후기 작성");
                        pointMap.put("pointDate", getTime());
                        pointMap.put("type", "savepoint");
                        pointMap.put("point", 50);
                        pointMap.put("userName", user.getUsername());

                        String pointID = databaseReference3.child(firebaseUser.getUid()).child("MyPoint").push().getKey();
                        databaseReference3.child(firebaseUser.getUid()).child("MyPoint").child(pointID).setValue(pointMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                databaseReference.child(product.getOrderId()).child(product.getEachOrderedId()).child("doReview").setValue("Yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
                showDialog();
            }
        });

    }


    // 갤러리에서 사진 가져오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_Code && resultCode == RESULT_OK)
        {
            imageUri =data.getData();
            uploadImage.setImageURI(imageUri);
        }
    }

    // 이미지 업로드
    private void uploadImagesAndSaveData() {
        if (!fn.isEmpty()) {
            if (imageUri != null) {
                // 이미지 업로드 로직
                StorageReference filePath1 = storageReference.child("image").child(imageUri.getLastPathSegment());
                filePath1.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            float rating = RatingBarEt.getRating();
                            reviewImage = imageUri.toString(); // reviewImage 저장
                            saveReviewData(rating);
                        } else {
                            Log.e("Image Upload", "Image upload failed: " + task.getException().getMessage());
                        }
                    }
                });
            } else {
                float rating = RatingBarEt.getRating();
                reviewImage = ""; // reviewImage를 빈 문자열로 저장
                saveReviewData(rating);
            }
        } else {
            showReivewDialog();
        }
    }

    // 리뷰 데이터 저장
    private void saveReviewData(float rating) {
        reviewId =  mRef.push().getKey();
        DatabaseReference productRef = mRef.child(String.valueOf(reviewId));
        productRef.child("pid").setValue(product.getProductId());
        productRef.child("pname").setValue(product.getProductName());
        productRef.child("pimg").setValue(product.getOrderImg());
        productRef.child("username").setValue(product.getUserName());
        productRef.child("pprice").setValue(product.getProductPrice());
        productRef.child("idToken").setValue(product.getUseridtoken());
        productRef.child("totalquantity").setValue(product.getTotalQuantity());
        productRef.child("rimage").setValue(reviewImage);
        productRef.child("rcontent").setValue(fn);
        productRef.child("rscore").setValue(rating);
        productRef.child("rdatetime").setValue(getTime());
        productRef.child("reviewid").setValue(reviewId);
    }

    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate2 = new Date(mNow);
        return mFormat.format(mDate2);
    }

    // 뒤로가기
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    // 후기 작성 완료 다이얼로그
    public void showDialog() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("후기 작성을 완료했습니다.\n감사합니다.");

        Button btnleft = dialog.findViewById(R.id.btn_left);
        btnleft.setText("후기 확인");
        btnleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewWriteActivity.this, ReviewHistoryActivity.class); // 후기 내역으로 이동
                startActivity(intent);
                finish();
            }
        });

        Button btnright = dialog.findViewById(R.id.btn_right);
        btnright.setText("홈 이동");
        btnright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewWriteActivity.this, MainActivity.class); // 메인으로 이동
                startActivity(intent);
                finish();
            }
        });
    }

    // 후기 작성 내용 미입력 다이얼로그
    public void showReivewDialog() {
        dialog2.show();

        TextView confirmTextView = dialog2.findViewById(R.id.confirmTextView);
        confirmTextView.setText("후기를 작성해주세요.");

        Button btnOk = dialog2.findViewById(R.id.btn_ok);
        btnOk.setText("확인");


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
    }
}


//package com.example.greeningapp;
//
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.RatingBar;
//import android.widget.TextView;
//import androidx.appcompat.widget.Toolbar;
//
//import com.bumptech.glide.Glide;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
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
//
//    private FirebaseAuth firebaseAuth;
//    private DatabaseReference databaseReference;
//
//    private DatabaseReference databaseReference2;
//    private DatabaseReference databaseReference3;
//    private FirebaseUser firebaseUser;
//
//
//    private BottomNavigationView bottomNavigationView;
//
//    private ImageButton navMain, navCategory, navDonation, navMypage;
//
//    FirebaseStorage mStorage;
//    ImageView uploadImage;
//    Button uploadBtn;
//    RatingBar RatingBarEt;
//    Uri imageUri=null;
//    //ImageButton cancelBtn;
//    EditText reviewEt;
//    MyOrder product = null;
//    private User user = null;
//    TextView Pname;
//    //TextView Pprice;
//    ImageView Pimg;
//    TextView mDate;
//
//    Dialog Reviewdialog;
//
//    private String orderId,myOrderId, eachOrderedId;
//    private int userSPoint;
//
//    Toolbar rtoolbar;
//
//    Dialog dialog;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_review_write);
//
//        dialog = new Dialog(ReviewWriteActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_confirm);
//
//        rtoolbar = findViewById(R.id.toolbar_reviewwrite);
//        setSupportActionBar(rtoolbar);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
//        actionBar.setDisplayShowTitleEnabled(false);//기본 제목 삭제.
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser").child(firebaseUser.getUid()).child("MyOrder");
//        databaseReference2 = FirebaseDatabase.getInstance().getReference("User");
//        databaseReference3 = FirebaseDatabase.getInstance().getReference("CurrentUser");
//
//        uploadBtn = findViewById(R.id.writeUploadBtn);
//        uploadImage = findViewById(R.id.writeUploadImage);
//        reviewEt = findViewById(R.id.writeReviewEt);
//        RatingBarEt = findViewById(R.id.writeRatingBar);
//
//        mDatabase=FirebaseDatabase.getInstance();
//        mRef=mDatabase.getReference().child("Review");
//        mStorage=FirebaseStorage.getInstance();
//        mDate = findViewById(R.id.reviewDate);
//
//        String dateTimeFormat = "yyyy.MM.dd";
//        Date date = Calendar.getInstance().getTime();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimeFormat);
//        String formattedDate = simpleDateFormat.format(date);
//        mDate.setText(formattedDate);
//
//        Pname = findViewById(R.id.writePname);
//        Pimg = (ImageView) findViewById(R.id.writePImg);
//
//        final Object object = getIntent().getSerializableExtra("product");
//
//        if(object instanceof MyOrder){
//            product = (MyOrder) object;
//            Log.d("ReviewWriteActivity", product+"");
//        }
//
//        if (product != null) {
//            Pname.setText(product.getProductName());
//            Glide.with(getApplicationContext()).load(product.getOrderImg()).into(Pimg);
//
//        }
//
//        uploadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent,Gallery_Code);
//            }
//        });
//
//    }
//
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
//        Button uploadBtn = findViewById(R.id.writeUploadBtn);
//        uploadBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String fn = reviewEt.getText().toString().trim();
//                String reviewImage = (imageUri != null) ? imageUri.toString() : "";
//
//                if (!fn.isEmpty() || reviewImage != null)  //후기작성 내용이 비어있지않으면 업로드 진행
//                {
//                    float rating = RatingBarEt.getRating();
//                    String reviewDate = mDate.getText().toString();
//
//
//                    String Pname = product.getProductName(); // 제품 이름을 String으로 저장
//                    String Pimg = product.getOrderImg(); // 제품 이미지 URL을 String으로 저장
//
//                    // Create a HashMap to store the review data
//                    HashMap<String, Object> reviewwriteMap = new HashMap<>();
//                    reviewwriteMap.put("pid", product.getProductId());
//                    reviewwriteMap.put("pname", product.getProductName());
//                    reviewwriteMap.put("pimg", product.getOrderImg());
//                    reviewwriteMap.put("username", product.getUserName());
//                    reviewwriteMap.put("idToken", product.getUseridtoken());
//                    reviewwriteMap.put("pprice", product.getProductPrice());
//                    reviewwriteMap.put("totalquantity", product.getTotalQuantity());
//                    reviewwriteMap.put("rimage", reviewImage);
//                    reviewwriteMap.put("rcontent", fn);
//                    reviewwriteMap.put("rscore", rating);
//                    reviewwriteMap.put("rdatetime", reviewDate);
//
//                    Log.d("Review", "리뷰 작성 여부 " + product.getDoReview());
//
//                    showReviewDialog();
//
//
//                    mRef.push().setValue(reviewwriteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//
//                            if (task.isSuccessful()) {
//                                databaseReference.child(product.getOrderId()).child(product.getEachOrderedId()).child("doReview").setValue("Yes").addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Log.d("Myreview", "myOrderId: " + myOrderId);
//                                        Log.d("eachorderid", "eachOrderedId: " + eachOrderedId);
//                                    }
//                                });
//
//                            } else {
//                                Log.e("Firebase", "Data write failed: " + task.getException().getMessage());
//                            }
//                        }
//                    });
//
//
//
//
//
//                }else{
//
//                }
//
//            }
//
//        });
//
//        // 하단바 구현
//        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_ReviewWrite);
//        bottomNavigationView.setSelectedItemId(R.id.tab_mypage);
//
//        // BottomNavigationView의 아이템 클릭 리스너 설정
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                if (item.getItemId() == R.id.tab_home) {
//                    // Home 액티비티로 이동
//                    startActivity(new Intent(ReviewWriteActivity.this, MainActivity.class));
//                    return true;
//                } else if (item.getItemId() == R.id.tab_shopping) {
//                    // Category 액티비티로 이동
//                    startActivity(new Intent(ReviewWriteActivity.this, CategoryActivity.class));
//                    return true;
//                } else if (item.getItemId() == R.id.tab_donation) {
//                    // Donation 액티비티로 이동
//                    startActivity(new Intent(ReviewWriteActivity.this, DonationMainActivity.class));
//                    return true;
//                } else if (item.getItemId() == R.id.tab_mypage) {
//                    // My Page 액티비티로 이동
//                    startActivity(new Intent(ReviewWriteActivity.this, MyPageActivity.class));
//                    return true;
//                }
//                return false;
//            }
//        });
//    }
//
//    //다이얼로그
//    public void showReviewDialog() {
//        dialog.show();
//
//        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
//        confirmTextView.setText("후기 작성이 완료되었습니다");
//
//        Button btnOk = dialog.findViewById(R.id.btn_ok);
//        btnOk.setText("홈으로 가기");
//
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                Intent intent = new Intent(ReviewWriteActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int itemId = item.getItemId();
//        if (itemId == android.R.id.home) {
//            onBackPressed();
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }
//}

package com.askmate;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.askmate.Adapter.Adapter_qna_answer;
//import com.askmate.Adapter.QnA_Adapter;
import com.askmate.Modal.QnA;
import com.askmate.Modal.Question;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.drjacky.imagepicker.ImagePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
//import com.askmate.R;

public class QnA_Ans extends AppCompatActivity implements Adapter_qna_answer.OnItemActionListener {
    ArrayList<String> uriString = new ArrayList<>();
    LinearLayoutCompat linearLayoutCompat;
    TextView anotherLayout;
    TextView questionName, questionCategory, questionDate, question;
    String questionProfile, uid, questionId, myanswerId, check;
    CircleImageView profileImage;
    ShapeableImageView questionImage;
    ShapeableImageView imageshow, image1;
    Uri answerUri;
    ProgressDialog progressDialog;
    String imageUrl;

NestedScrollView scrollView;
    boolean imageCheck = false;
    boolean clickCheck = false;


    RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;


    private ArrayList<QnA> answerList;
//    ProgressDialog progressDialog;
    private Adapter_qna_answer adapter;
    private DatabaseReference questionsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qn_aans);


        linearLayoutCompat = findViewById(R.id.linearLayoutCompat_qna_ans);
        anotherLayout = findViewById(R.id.anotherLayout);
questionName = findViewById(R.id.name);
questionCategory = findViewById(R.id.category);
questionDate = findViewById(R.id.date);
question = findViewById(R.id.question);
questionImage = findViewById(R.id.imageQuestion);
profileImage = findViewById(R.id.profile_image);

        shimmerFrameLayout = findViewById(R.id.shimmer);
        shimmerFrameLayout.startShimmer();
        getWindow().setStatusBarColor(Color.parseColor("#eff0f7"));
        recyclerView =  findViewById(R.id.recyclerView);
scrollView = findViewById(R.id.scrollView);
        progressDialog = new ProgressDialog(this);

//        Intent intent = getIntent();
        check = getIntent().getStringExtra("check");

        if(check.equals("QnA"))
        {
            loadDatafromQnA();
        }
        else if(check.equals("myAns"))
        {
            questionId = getIntent().getStringExtra("myQuestionId");
            uid = getIntent().getStringExtra("uid");
            myanswerId = getIntent().getStringExtra("myAnswerId");
            loadDatafrommyAnswers();
        }
        else
        {
//            Toast.makeText(this, "Somewhere", Toast.LENGTH_SHORT).show();
            questionId = getIntent().getStringExtra("myQuestionId");
            uid = getIntent().getStringExtra("uid");
            loadDatafromMyQnA();
        }

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        answerList = new ArrayList<>();
//        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY;
        adapter = new Adapter_qna_answer(this, questionId, answerList, this, myanswerId, recyclerView);
        recyclerView.setAdapter(adapter);

        questionsRef = FirebaseDatabase.getInstance().getReference().child("askmate").child("questions").child(questionId).child("answers");
        loadQuestions();




        questionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fullImageIntent = new Intent(QnA_Ans.this, FulllScreenImage.class);
//                // uriString is an ArrayList<String> of URI of all images
//                uriString.add(imageUrl);
                fullImageIntent.putExtra("url", imageUrl);
//                Toast.makeText(QnA_Ans.this, imageUrl, Toast.LENGTH_SHORT).show();
                Log.d("url", "onClick: " + imageUrl);

                startActivity(fullImageIntent);
            }
        });


        linearLayoutCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


    }

    private void loadDatafrommyAnswers() {

        FirebaseDatabase.getInstance().getReference().child("askmate").child("questions")
                .child(questionId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String Question = snapshot.child("question").getValue(String.class);
                        String Category = snapshot.child("category").getValue(String.class);
                        String TimeStamp = snapshot.child("timestamp").getValue(String.class);
                        String image = snapshot.child("image").getValue(String.class);
                        String questionaskID = snapshot.child("uid").getValue(String.class);
                        question.setText(Question);
                        questionCategory.setText(Category);
                        questionDate.setText(getTimeAgo(TimeStamp));
                        uid = getIntent().getStringExtra("uid");
                        Log.d("data", "Data question--> : "+  image + ""+ questionId +" "+ Question + " " + Category);


                        if(image != null  && !image.isEmpty() ) {

                            questionImage.setVisibility(View.VISIBLE);
                            Glide.with(QnA_Ans.this).load(image).into(questionImage);
                            imageUrl = image;

                        }
                        else
                        {
                            questionImage.setVisibility(View.GONE);

                        }

                        FirebaseDatabase.getInstance().getReference().child("users").child(questionaskID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String image = snapshot.child("image").getValue(String.class);
                                String name = snapshot.child("name").getValue(String.class);
//                Log.d("image", "onDataChange: " + image);
                                questionName.setText(name);

                                Glide.with(QnA_Ans.this).load(image).into(profileImage);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

//                         Add a slight delay before calling highlightAnswer
//                        new android.os.Handler().postDelayed(
//                                () -> highlightAnswer(answerId),
//                                500 // 100 milliseconds delay
//                        );

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private int findPositionOfAnswer(String answerId) {
        // Iterate through your answerModalList to find the position of the answer
        for (int i = 0; i < answerList.size(); i++) {
            if (answerList.get(i).getAnswerid().equals(answerId)) {
                Log.d("ID", "answerId value found: " + i);

                return i;
            }
        }
        Log.d("ID", "Not found");

        return RecyclerView.NO_POSITION; // Answer not found
    }

    private void loadDatafromMyQnA() {

        FirebaseDatabase.getInstance().getReference().child("askmate").child("questions")
                .child(questionId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
String Question = snapshot.child("question").getValue(String.class);
String Category = snapshot.child("category").getValue(String.class);
String TimeStamp = snapshot.child("timestamp").getValue(String.class);
String image = snapshot.child("image").getValue(String.class);
question.setText(Question);
questionCategory.setText(Category);
questionDate.setText(getTimeAgo(TimeStamp));



                        if(image != null  && !image.isEmpty() ) {

                            questionImage.setVisibility(View.VISIBLE);
                            Glide.with(QnA_Ans.this).load(image).into(questionImage);
                            imageUrl = image;
                        }
                        else
                        {
                            questionImage.setVisibility(View.GONE);

                        }


                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String image = snapshot.child("image").getValue(String.class);
                                String name = snapshot.child("name").getValue(String.class);
//                Log.d("image", "onDataChange: " + image);
                                questionName.setText(name);

                                Glide.with(QnA_Ans.this).load(image).into(profileImage);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




//        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                String image = snapshot.child("image").getValue(String.class);
//                String name = snapshot.child("name").getValue(String.class);
////                Log.d("image", "onDataChange: " + image);
//                questionName.setText(name);
//
//                Glide.with(QnA_Ans.this).load(image).into(profileImage);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    }

    private void loadDatafromQnA()
    {
        questionName.setText(getIntent().getStringExtra("name"));
        question.setText(getIntent().getStringExtra("question"));
        questionDate.setText(getIntent().getStringExtra("time"));
        questionCategory.setText(getIntent().getStringExtra("category"));
        String image = getIntent().getStringExtra("image");
        questionId = getIntent().getStringExtra("questionId");
        uid = getIntent().getStringExtra("uid");

        if(image != null  && !image.isEmpty() ) {

            questionImage.setVisibility(View.VISIBLE);
            imageUrl = image;

            Glide.with(this).load(getIntent().getStringExtra("image")).into(questionImage);
        }
        else
        {
            questionImage.setVisibility(View.GONE);

        }


        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String image = snapshot.child("image").getValue(String.class);
                Log.d("image", "onDataChange: " + image);
                Glide.with(QnA_Ans.this).load(image).into(profileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void loadQuestions() {
        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                answerList.clear();
                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                    QnA question = questionSnapshot.getValue(QnA.class);


                    String answerId = questionSnapshot.getKey();

                    // Extract other properties of the answer
                    String name = questionSnapshot.child("name").getValue(String.class);
                    String timestamp = questionSnapshot.child("timestamp").getValue(String.class);
                    String answer = questionSnapshot.child("answer").getValue(String.class);
                    String like = questionSnapshot.child("like").getValue(String.class);
                    String image = questionSnapshot.child("image").getValue(String.class);
                    String uid = questionSnapshot.child("uid").getValue(String.class);

                    // Create QnA object and add it to the answerList
                    QnA answerItem = new QnA(name, timestamp, answer, like, image, uid, answerId);
                    answerList.add(0, answerItem);

//                    answerList.add(0,question);
//                    Log.d("loadQuestions", "Before how many times ----> " + myanswerId);

                }
                
                
//                Log.d("loadQuestions", "All data loaded, calling highlightAnswer" + myanswerId);



                adapter.notifyDataSetChanged();

                // Show total value after the layout is complete
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        showTotalvalue();
                    }
                });
                
//                showTotalvalue();
                // Scroll to the highlighted answer position if myanswerId is not empty
                if (!TextUtils.isEmpty(myanswerId)) {
                    adapter.setHighlightedAnswerId(myanswerId);
                    int position = findPositionOfAnswer(myanswerId);
                    if (position != RecyclerView.NO_POSITION) {
                        recyclerView.postDelayed(() -> {
                            recyclerView.scrollToPosition(position);
                        }, 200); // Delay in milliseconds
                    }

//                    final float y = recyclerView.getChildAt(position).getY();

//                    final int position = findPositionOfAnswer(myanswerId);
//                    if (position != RecyclerView.NO_POSITION) {
//                        final float y = recyclerView.getChildAt(position).getY();
//                        scrollView.postDelayed(() -> {
//                            scrollView.smoothScrollTo(0, (int) y);
//                        }, 200); // Delay in milliseconds
//                    }

//                    scrollView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            scrollView.fling(0);
//                            scrollView.smoothScrollTo(0, (int) y);
//                        }
//                    });



//                    final float y = recyclerView.getY() + recyclerView.getChildAt(findPositionOfAnswer(myanswerId)).getY();
//                    scrollView.post(() -> {
//                        scrollView.fling(0);
//                        scrollView.smoothScrollTo(pos);
//                    });

                }


                if (adapter.getItemCount() == 0) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    anotherLayout.setVisibility(View.VISIBLE);
                } else {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
//                    int position = findPositionOfAnswer(myanswerId);


//                    if (position != RecyclerView.NO_POSITION) {
//                        recyclerView.postDelayed(() -> {
////                            recyclerView.scrollToPosition(position);
//
//                        }, 200); // Delay in milliseconds
//                    }

                    anotherLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

//        if (adapter.getItemCount() == 0) {
//            recyclerView.setVisibility(View.GONE);
//            anotherLayout.setVisibility(View.VISIBLE);
//        } else {
//            recyclerView.setVisibility(View.VISIBLE);
//            anotherLayout.setVisibility(View.GONE);
//        }
    }

    private void showTotalvalue() {

//        Log.d("showTotalvalue", "showTotalvalue: " + recyclerView.getChildCount());

        Log.d("position", "Count: " + recyclerView.getChildCount());
        int position = findPositionOfAnswer(myanswerId);

        if (recyclerView.getChildAt(position) != null)
        {
            final float y = recyclerView.getY() + recyclerView.getChildAt(position).getY();
        scrollView.post(() -> {  scrollView.fling(0);
            scrollView.smoothScrollTo(0, (int) y);
        });
//        if (recyclerView.getChildAt(position) != null) {
//            final float y = recyclerView.getChildAt(position).getY();
//            Log.d("position", "Count value: " + y);
//                                scrollView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            scrollView.fling(0);
//                            scrollView.smoothScrollTo(0, (int) y);
//                        }
//                    });
        }
        else
        {
            Log.d("position", "Count value: " + "Empty");

        }
    }


    private void showDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_asnwer);
        AppCompatButton button = dialog.findViewById(R.id.Button);
        TextView addImage = dialog.findViewById(R.id.addImage);
        FrameLayout fl = dialog.findViewById(R.id.frameHide);
TextInputEditText answer = dialog.findViewById(R.id.answer_asked);


//        Toast.makeText(this, questionId, Toast.LENGTH_SHORT).show();
        imageshow = dialog.findViewById(R.id.imageshow);
        ImageView closeButton = dialog.findViewById(R.id.closeButton);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of imageshow ConstraintLayout
                if (fl.getVisibility() == View.VISIBLE) {
                    fl.setVisibility(View.GONE);
                    imageCheck = false;
                    clickCheck = false;
                } else {
                    fl.setVisibility(View.VISIBLE);
                    imageCheck = true;
                    clickCheck = false;
                }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the imageshow ConstraintLayout
                fl.setVisibility(View.GONE);
                imageCheck = false;
                clickCheck = false;
            }
        });

        imageshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launcher1.launch(ImagePicker.Companion.with(QnA_Ans.this)
                        .crop()
                        .cropFreeStyle()
                        .galleryOnly()
                        .setOutputFormat(Bitmap.CompressFormat.JPEG)
                        .createIntent()
                );


            }

        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answerText = answer.getText().toString();
                String timestamp = String.valueOf(System.currentTimeMillis());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Uploading answer...");
                progressDialog.show();

                if(answerText.isEmpty())
                {
                    Toast.makeText(QnA_Ans.this, "Please enter answer", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                else
                {
                    if (answerUri != null)
                    {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("askmate")
                                .child("questions").child(questionId).child("answers");

                        String pushKey = reference.push().getKey();

                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        StorageReference imageRef = storageRef.child(FirebaseAuth.getInstance().getUid()).child("askmate").child(questionId)
                                .child(pushKey);

                        imageRef.putFile(answerUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        HashMap<String, Object> withImage = new HashMap<>();
                                        withImage.put("answer", answerText);
                                        withImage.put("timestamp", timestamp);
                                        withImage.put("likes", null);
                                        withImage.put("uid", FirebaseAuth.getInstance().getUid());
                                        withImage.put("image", uri.toString());

                                        reference.child(pushKey).updateChildren(withImage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                HashMap<String, Object> myAnswer = new HashMap<>();
                                                myAnswer.put(pushKey, questionId);
                                                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                                                                .child("answers").updateChildren(myAnswer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                            Toast.makeText(QnA_Ans.this, "Answer uploaded", Toast.LENGTH_SHORT).show();
                                                                Toast.makeText(QnA_Ans.this, "Answer uploaded", Toast.LENGTH_SHORT).show();
                                                                                progressDialog.dismiss();
                                                                                loadQuestions();
                                                                                dialog.dismiss();


                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                                progressDialog.dismiss();
                                                            }
                                                        });

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(QnA_Ans.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();


                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(QnA_Ans.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(QnA_Ans.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                            }
                        });

                    }
                    else
                    {


                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("askmate")
                                .child("questions").child(questionId).child("answers");

                        String pushKey = reference.push().getKey();
                        HashMap<String, Object> withoutImage = new HashMap<>();
                        withoutImage.put("answer", answerText);
                        withoutImage.put("timestamp", timestamp);
                        withoutImage.put("likes", null);
                        withoutImage.put("uid", FirebaseAuth.getInstance().getUid());

                        reference.child(pushKey).updateChildren(withoutImage).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                HashMap<String, Object> myAnswer = new HashMap<>();
                                myAnswer.put(pushKey, questionId);
                                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("answers")
                                        .updateChildren(myAnswer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                dialog.dismiss();
                                                loadQuestions();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(QnA_Ans.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(QnA_Ans.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });



                    }


                }
            }
        });




        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private String getTimeAgo(String timestamp) {
        long timeInMillis = Long.parseLong(timestamp);
        long now = System.currentTimeMillis();
        long diff = now - timeInMillis;

        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        if (seconds < 60) {
            return "just now";
        } else if (minutes < 60) {
            return minutes + " min ago";
        } else if (hours < 24) {
            return hours + " hour ago";
        } else if (days == 1) {
            return "yesterday";
        } else if (days < 7) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());
            Date date = new Date(timeInMillis);
            return sdf.format(date);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
            Date date = new Date(timeInMillis);
            return sdf.format(date);
        }
    }

    ActivityResultLauncher<Intent> launcher1 =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    //                    uri2  = uri;

                    if (uri != null && !uri.toString().isEmpty()) {
                        //                        imageUriCache.add(uri);
                        imageshow.setImageURI(uri);
                        imageshow.setTag(R.id.source, "gallery");
                        answerUri = uri;
                        imageCheck = true;
                        clickCheck = true;

                    } else {
                        // Handle the case where URI is null or empty
                        Toast.makeText(QnA_Ans.this, "Failed to get image. Please try again.", Toast.LENGTH_SHORT).show();
                        clickCheck = false;

                    }
                } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                    // Use ImagePicker.Companion.getError(result.getData()) to show an error
                    Toast.makeText(QnA_Ans.this, "Error while picking image. Please try again.", Toast.LENGTH_SHORT).show();
                    clickCheck = false;

                }
            });



    @Override
    public void onDeleteItem(QnA QnaModal) {
//        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Deleting your answer...");
        progressDialog.show();

questionsRef.child(QnaModal.getAnswerid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
    @Override
    public void onSuccess(Void unused) {

        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("answers")
                .child(QnaModal.getAnswerid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();

                        loadQuestions();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        Toast.makeText(QnA_Ans.this, "Something went wrong, please try again after sometime", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
        progressDialog.dismiss();

        Toast.makeText(QnA_Ans.this, "Something went wrong, please try again after sometime", Toast.LENGTH_SHORT).show();
    }
});
    }

}
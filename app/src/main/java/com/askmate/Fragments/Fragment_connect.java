package com.askmate.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

//import com.askmate.Adapter.QnA_Adapter;
import com.askmate.Interface.CheckExistsCallback;
import com.askmate.Modal.QnA;
import com.askmate.Notes.AdapterUpload;
import com.askmate.Notes.NotesModal;
import com.askmate.R;
import com.askmate.SplashScreen.SplashScreen;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_connect#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_connect extends Fragment {
    AppCompatButton Button;
    LinearLayoutCompat linearLayoutCompat_first;
//    LinearLayoutCompat linearLayoutCompat;
    RecyclerView recyclerView;
    AppBarLayout appBarLayout;
    NestedScrollView nestedScrollView;
    TextView NotifyText, coachingName;
    String holdUid;
    double latitude;
    double longitude;
    String cityLocality;
    ArrayList<NotesModal> modals;
    private AdapterUpload adapterUpload;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String uid;

    public Fragment_connect() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_connect.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_connect newInstance(String param1, String param2) {
        Fragment_connect fragment = new Fragment_connect();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect, container, false);
        Button = view.findViewById(R.id.Button);
        linearLayoutCompat_first = view.findViewById(R.id.linear1st);
//        linearLayoutCompat = view.findViewById(R.id.linearLayoutCompat_qna_ans);
        recyclerView = view.findViewById(R.id.recyclerView);
        nestedScrollView = view.findViewById(R.id.scrollview_recyclerview);
        appBarLayout = view.findViewById(R.id.htab_appbar);
        NotifyText = view.findViewById(R.id.notifyText);
        coachingName = view.findViewById(R.id.Coaching_name);
        SharedPreferences preferences = getActivity().getSharedPreferences("Location", MODE_PRIVATE);
        latitude = preferences.getFloat("latitude", 0.0f);  // Default value is 0.0 if not found
        longitude = preferences.getFloat("longitude", 0.0f);
        // Check if latitude and longitude are not 0 (indicating default values)
        if (latitude != 0f && longitude != 0f) {
            // Use Geocoder to get the locality from latitude and longitude
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    cityLocality = address.getLocality();
                    // Now you have the locality from latitude and longitude
                    Log.d("Locality", "Locality: " + cityLocality);
                } else {
                    Log.d("Locality", "No address found for the given coordinates.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Locality", "Error getting address from Geocoder: " + e.getMessage());
            }
        } else {
            Log.d("Locality", "Latitude and longitude not found in SharedPreferences.");
        }


        SharedPreferences saveisConnectpreference = getActivity().getSharedPreferences("isConnect", MODE_PRIVATE);
//        if (NotifyText.getText().toString().isEmpty() || NotifyText.getText().toString().equals("Please wait for approval")) {
        if ( NotifyText.getText().toString().equals("Please wait for approval") || NotifyText.getText().toString().equals("Your Coaching didn't upload any notes yet!")) {

            Button.setEnabled(false);
            Button.setVisibility(View.GONE);
        } else{
            Button.setEnabled(true);
            Button.setVisibility(View.VISIBLE);
        }


                SharedPreferences getId = getActivity().getSharedPreferences("connectID", MODE_PRIVATE);
        String uid = getId.getString("uid", null);
        if (uid != null && !uid.trim().isEmpty()) {
            Log.d("uid", "onCreateView: " + uid);
   linearLayoutCompat_first.setVisibility(View.GONE);
                                 appBarLayout.setVisibility(View.VISIBLE);
                                 recyclerView.setVisibility(View.VISIBLE);
                                 nestedScrollView.setVisibility(View.VISIBLE);

                                 modals = new ArrayList<>();
            adapterUpload = new AdapterUpload(null);

//                                 recyclerView.setLayoutManager(LinearLayoutManager);
            adapterUpload = new AdapterUpload(modals);
recyclerView.setAdapter(adapterUpload);


            loadDataFromFirebase(uid);

        }

        else
        {
            linearLayoutCompat_first.setVisibility(View.VISIBLE);
            appBarLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.GONE);

            FirebaseDatabase.getInstance().getReference()
                    .child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
//                        for (DataSnapshot datasnapshot: snapshot.getChildren()) {
                            String checkValue = snapshot.child("isConnected").getValue(String.class);
                            if (checkValue != null) {

                                if (checkValue.equals("waiting")) {
                                    NotifyText.setText("Please wait for approval");
                                    Button.setEnabled(false);
                                    Button.setVisibility(View.GONE);
                                }
                                else {
                                    String uid = snapshot.child("isConnected").getValue(String.class);
                                    Log.d("uid", "onDataChange:---> " + uid);
                                    SharedPreferences connectID = getActivity().getSharedPreferences("connectID", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = connectID.edit();
                                    editor.putString("uid", uid);
                                    editor.commit();
                                    NotifyText.setText("You are connected to your Coaching! \n Kindly restart AskMate");
                                    Button.setEnabled(false);
                                    Button.setVisibility(View.GONE);
                                }

                            } else {
                                NotifyText.setText("You are not connected to your Coaching!");
                                Button.setEnabled(true);
                                Button.setVisibility(View.VISIBLE);
                            }
//                        }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }



        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
//                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        recyclerView.setAdapter(adapter);
//
//        linearLayoutCompat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDialog_ask();
//            }
//        });

        return view;
    }

    private void showDialog() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        TextInputEditText codeText = dialog.findViewById(R.id.code);
        AppCompatButton button = dialog.findViewById(R.id.Button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                String code = codeText.getText().toString();
                if (code.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter code", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                } else {

                    FirebaseDatabase.getInstance().getReference().child("Coachings")
                            .child("register")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean exists = false;
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if (dataSnapshot.getKey().equals(code)) {
                                            holdUid = dataSnapshot.getValue(String.class);
                                            exists = true;
                                            break;
                                        }
                                    }

                                    if (exists) {

                                        FirebaseDatabase.getInstance().getReference()
                                                .child("users").child(FirebaseAuth.getInstance().getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        String name = snapshot.child("name").getValue(String.class);
                                                        String number = snapshot.child("number").getValue(String.class);
                                                        String myUid = FirebaseAuth.getInstance().getUid();
                                                        uid = holdUid;
                                                        Log.d("uid", "onDataChange: " + holdUid + " " + uid);
                                                        HashMap<String, Object> myData = new HashMap<>();
                                                        myData.put("name", name);
                                                        myData.put("phone", number);
                                                        FirebaseDatabase.getInstance().getReference().child("Coachings")
                                                                .child(cityLocality)
                                                                .child(uid)
                                                                .child("attendance")
                                                                .child(myUid)
                                                                .updateChildren(myData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
//                                                                        FirebaseDatabase.getInstance().getReference()
//                                                                                .child("users").child(FirebaseAuth.getInstance().getUid())
//                                                                                        .setValue("isConnected", "waiting").addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                                    @Override
//                                                                                    public void onSuccess(Void unused) {
//                                                                                        progressDialog.dismiss();
//
//                                                                                        Toast.makeText(getActivity(), "Please wait for approval",
//                                                                                                Toast.LENGTH_SHORT).show();
//                                                                                    }
//                                                                                }).addOnFailureListener(new OnFailureListener() {
//                                                                                    @Override
//                                                                                    public void onFailure(@NonNull Exception e) {
//                                                                                        progressDialog.dismiss();
//
//                                                                                        Toast.makeText(getActivity(), "Try again later",
//                                                                                                Toast.LENGTH_SHORT).show();
//                                                                                    }
//                                                                                });
////                                                                        progressDialog.dismiss();
//
////                                                                        Toast.makeText(getActivity(), "Please wait for approval",
////                                                                                Toast.LENGTH_SHORT).show();

                                                                        // Get reference to the user's node
                                                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                                                                .child("users")
                                                                                .child(FirebaseAuth.getInstance().getUid());
// Create a map of updates
                                                                        Map<String, Object> updates = new HashMap<>();
                                                                        updates.put("isConnected", "waiting");

// Update the user's node
                                                                        userRef.updateChildren(updates)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(getActivity(), "Please wait for approval", Toast.LENGTH_SHORT).show();
                                                                                        dialog.dismiss();
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(getActivity(), "Try again later", Toast.LENGTH_SHORT).show();
                                                                                        dialog.dismiss();

                                                                                    }
                                                                                });

                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        progressDialog.dismiss();

                                                                        Toast.makeText(getActivity(), "Try again later",
                                                                                Toast.LENGTH_SHORT).show();
                                                                        dialog.dismiss();

                                                                    }
                                                                });


//                        Log.d("imageProfile", "onDataChange: " + imageProfile);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                    }
                                    if (!exists) {
                                        progressDialog.dismiss();

                                        Toast.makeText(getActivity(), "Oops! It looks like the code is incorrect or the coaching center couldn't be found.", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();


                                    }

//                                    callback.onResult(exists);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle potential errors
//                                    callback.onResult(false); // Return false or handle differently if you wish
                                }
                            });


//                    checkExists("someCode", new CheckExistsCallback() {
//                        @Override
//                        public void onResult(boolean exists) {
//                            if (exists) {
//                                // Code exists
//                                Toast.makeText(getActivity(), "Code exists!", Toast.LENGTH_SHORT).show();
//                            } else {
//                                // Code does not exist
//                                Toast.makeText(getActivity(), "Oops! It looks like the code is incorrect or the coaching center couldn't be found.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });

//                    if (checkExists(code))
//                    {
//
//                    }
//                    else
//                    {
//                        Toast.makeText(getActivity(), "Oops! It looks like the code is incorrect or the coaching center couldn't be found.", Toast.LENGTH_SHORT).show();
//
//                    }

                }


//                Intent intent = new Intent(getActivity(), SplashScreen.class);
//                SharedPreferences preferences = getActivity().getSharedPreferences("isConnect", MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putBoolean("Connected", true);
//                boolean isSuccess = editor.commit();
//                if (isSuccess)
//                {
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    if (getActivity() != null) {
//                        getActivity().finish();
//                    }
//                }
//                else
//                {
//                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
//                }

            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

//    private boolean checkExists(String code) {
//
//        FirebaseDatabase.getInstance().getReference().child("Coachings")
//                .child("register")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                      for (DataSnapshot dataSnapshot : snapshot.getChildren())
//                      {
//                          if (dataSnapshot.getKey().equals(code))
//                          {
//                              return true;
//                          }
//                      }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//    }

    private void checkExists(String code, CheckExistsCallback callback) {
        FirebaseDatabase.getInstance().getReference().child("Coachings")
                .child("register")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean exists = false;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.getKey().equals(code)) {
                                exists = true;
                                break;
                            }
                        }

                        callback.onResult(exists);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle potential errors
                        callback.onResult(false); // Return false or handle differently if you wish
                    }
                });
    }

    private void showDialog_ask() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_qna_asked);
        AppCompatButton button = dialog.findViewById(R.id.Button);


        String[] languages = getResources().getStringArray(R.array.subjects);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, languages);

        MaterialAutoCompleteTextView autocompleteTV = dialog.findViewById(R.id.autoCompleteTextView);

        autocompleteTV.setAdapter(arrayAdapter);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                Intent intent = new Intent(getActivity(), SplashScreen.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                if (getActivity() != null) {
//                    getActivity().finish();
//                }
            }
        });
//
//        LinearLayoutCompat postcreate = dialog.findViewById(R.id.post_linear_layout);
//        LinearLayoutCompat AskCreate = dialog.findViewById(R.id.Ask_linear_layout);
//


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void loadDataFromFirebase(String uid) {
        loadName(uid);
//        Toast.makeText(getActivity(), "I'm running", Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference().child("Coachings")
                .child(cityLocality).child(uid)
                .child("notes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modals.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String key = dataSnapshot.getKey();
                            String text = dataSnapshot.child("text").getValue(String.class);
                            String link = dataSnapshot.child("link").getValue(String.class);
                            Log.d("data", "onDataChange: " + uid + " "
                             + text + " " + link);
                            NotesModal modal = new NotesModal(link, text, key);
                            modals.add(modal);
                        }
                        if (modals.isEmpty()) {
                            linearLayoutCompat_first.setVisibility(View.VISIBLE);
                            appBarLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            nestedScrollView.setVisibility(View.GONE);
                            NotifyText.setText("Your Coaching didn't upload any notes yet!");
                            Button.setEnabled(false);
                            Button.setVisibility(View.GONE);


                        } else {
//                            binding.firstLayout.setVisibility(View.VISIBLE);
//                            binding.secondLayout.setVisibility(View.GONE);
                            adapterUpload.notifyDataSetChanged();
                        }
//                        updateUI();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadName(String uid) {

        FirebaseDatabase.getInstance().getReference().child("Coachings").child(cityLocality)
                .child(uid).child("coaching_profiles").child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.getValue(String.class);
                        coachingName.setText(name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


//
//    @Override
//    public void onResult(boolean exists) {
//
//    }

//
//    @Override
//    public void onResult(boolean exists) {
//
//    }
}
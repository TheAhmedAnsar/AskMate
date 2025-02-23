package com.askmate.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.askmate.Adapter.MyAdapter;
import com.askmate.R;
import com.askmate.setting_profile;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_profile extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
CircleImageView profileImage;
TextView username, userContact;
ImageView setting;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_profile.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_profile newInstance(String param1, String param2) {
        fragment_profile fragment = new fragment_profile();
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
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        tabLayout=view.findViewById(R.id.htab_tabs);
        viewPager=view.findViewById(R.id.htab_viewpager);
profileImage = view.findViewById(R.id.profileImage);
username = view.findViewById(R.id.userName);
userContact = view.findViewById(R.id.userNumber);
setting = view.findViewById(R.id.settingProfile);


        tabLayout.addTab(tabLayout.newTab().setText("My Questions"));
        tabLayout.addTab(tabLayout.newTab().setText("My Answers"));

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), setting_profile.class);
                startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String imageProfile = snapshot.child("image").getValue(String.class);
                        String name = snapshot.child("name").getValue(String.class);
                        String number = snapshot.child("number").getValue(String.class);

                        if (isAdded()) {
                            Glide.with(getActivity()).load(imageProfile)
                                    .placeholder(R.drawable.user)
                                    .into(profileImage);
                        }
                       username.setText(name);
                        String mynumber = "+91 " + number;
                        userContact.setText(mynumber);


//                        Log.d("imageProfile", "onDataChange: " + imageProfile);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//        tabLayout.setTabTextColors(Color.parseColor("dgfdgfg"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        tabLayout.addTab(tabLayout.newTab().setText("Movie"));

         MyAdapter adapter = new MyAdapter(getActivity(),getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);


//     viewPager.setOnPageChangeListener(new TabLayout.Tab);
//        viewPager.setOnPageChangeListener((CustomViewPager_new.OnPageChangeListener) new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }
}
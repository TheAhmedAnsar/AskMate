package com.askmate.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.askmate.Fragments.my_answer;
import com.askmate.Fragments.my_question;

public class MyAdapter extends FragmentPagerAdapter {
  
    private Context myContext;
    int totalTabs;  
  
    public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);  
        myContext = context;  
        this.totalTabs = totalTabs;  
    }  
  
    // this is for fragment tabs  
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {  
            case 0:

                my_question myquestion = new my_question();
                return myquestion;

            case 1:
                my_answer myanswer = new my_answer();
                return myanswer;
//            case 2:
//                MovieFragment movieFragment = new MovieFragment();
//                return movieFragment;
            default:  
                return null;  
        }  
    }  
// this counts total number of tabs  
    @Override  
    public int getCount() {  
        return totalTabs;  
    }  
}  
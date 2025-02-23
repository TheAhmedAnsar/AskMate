package com.askmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> fragmentTitle = new ArrayList<>();
    private final List<String> fragmentTags = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm)
    {
        super(fm);
    }

    public void add(Fragment fragment, String title, String tag)
    {
        fragments.add(fragment);
        fragmentTitle.add(title);
        fragmentTags.add(tag);


    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {

        return fragments.get(position);
    }


//    @NonNull
//    @Override
//    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                return new fragment_provided_courses();
//            case 1:
//                return new fragment_homepage();
//
//            default:
//                return null;
//        }
//    }

    @Override public int getCount()
    {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return fragmentTitle.get(position);
    }
}

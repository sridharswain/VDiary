package com.example.sridh.vdiary;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Sparsha Saha on 9/11/2016.
 */
public class SwipeAdapter extends FragmentPagerAdapter{
    ArrayList<Fragment> fragments=new ArrayList<>();
    public SwipeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addfragment(Fragment f)
    {
        fragments.add(f);
    }
}

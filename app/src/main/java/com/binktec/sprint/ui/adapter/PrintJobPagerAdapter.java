package com.binktec.sprint.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.binktec.sprint.ui.fragment.HistoryFragment;
import com.binktec.sprint.ui.fragment.ProgressFragment;

public class PrintJobPagerAdapter extends FragmentStatePagerAdapter {
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private static final int tabCount = 2;
    public PrintJobPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ProgressFragment.newInstance();
            case 1:
                return HistoryFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "Ongoing";
            case 1:
                return "History";
        }
        return null;
    }


}
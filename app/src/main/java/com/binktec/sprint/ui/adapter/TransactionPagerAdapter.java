package com.binktec.sprint.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.binktec.sprint.ui.fragment.ChooseFileFragment;
import com.binktec.sprint.ui.fragment.ChooseShopFragment;
import com.binktec.sprint.ui.fragment.PrintDetailFragment;

public class TransactionPagerAdapter extends FragmentPagerAdapter {
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public TransactionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return ChooseFileFragment.newInstance();
            case 1:
                return PrintDetailFragment.newInstance();
            case 2:
                return ChooseShopFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 6 total pages.
        return 3;
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
        return "SECTION "+(position+1);
    }
}
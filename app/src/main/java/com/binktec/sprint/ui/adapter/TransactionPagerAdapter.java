package com.binktec.sprint.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.binktec.sprint.ui.fragment.ChooseFileFragment;
import com.binktec.sprint.ui.fragment.ChooseShopFragment;
import com.binktec.sprint.ui.fragment.PrintDetailFragment;

public class TransactionPagerAdapter extends FragmentPagerAdapter {
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
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "SECTION "+(position+1);
    }
}
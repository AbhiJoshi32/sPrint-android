package com.binktec.sprint.ui.fragment;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.binktec.sprint.R;

public class SettingFragment extends PreferenceFragment {


    public SettingFragment() {
    }

    public static SettingFragment mewInstance() {
        return new SettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}

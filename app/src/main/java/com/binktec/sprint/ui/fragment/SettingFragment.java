package com.binktec.sprint.ui.fragment;


import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.AvailShopFragmentListener;
import com.binktec.sprint.interactor.fragment.SettingFragmentListener;

public class SettingFragment extends ListFragment {

    SettingFragmentListener settingFragmentListener;

    public SettingFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SettingFragmentListener) {
            settingFragmentListener = (SettingFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement settingFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        settingFragmentListener = null;
    }

    public static SettingFragment mewInstance() {
        return new SettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] values = new String[] { "Instruction", "Help"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) {
            settingFragmentListener.openInstructionActivity();
        } else if (position == 1) {
            settingFragmentListener.openHelpActivity();
        }
    }

}
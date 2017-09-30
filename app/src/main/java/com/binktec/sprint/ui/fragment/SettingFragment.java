package com.binktec.sprint.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.binktec.sprint.interactor.fragment.SettingFragmentListener;
import com.binktec.sprint.ui.activity.HelpActivity;
import com.binktec.sprint.ui.activity.InstructionActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] values = new String[] { "Instructions", "Help","Change Password","Logout"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Class newActivity;
        if (position == 0) {
            settingFragmentListener.openNewActivity(InstructionActivity.class);
        } else if (position == 1) {
            settingFragmentListener.openNewActivity(HelpActivity.class);
        } else if (position == 2) {
            settingFragmentListener.changePassClicked();
        } else if (position == 3) {
            settingFragmentListener.logoutClicked();
        }

    }
}
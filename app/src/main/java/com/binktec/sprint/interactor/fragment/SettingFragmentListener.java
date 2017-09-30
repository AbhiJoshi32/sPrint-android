package com.binktec.sprint.interactor.fragment;

import com.binktec.sprint.ui.activity.InstructionActivity;

public interface SettingFragmentListener {
    void openNewActivity(Class activityClass);

    void changePassClicked();

    void logoutClicked();
}

package com.binktec.sprint.interactor.fragment;

public interface SettingFragmentListener {
    void openNewActivity(Class activityClass);

    void changePassClicked();

    void logoutClicked();
}

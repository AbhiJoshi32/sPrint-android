package com.binktec.sprint.presenter;

import android.net.Uri;

import com.binktec.sprint.interactor.presenter.SettingPresenterListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingPresenter {
    private SettingPresenterListener settingPresenterListener;

    public SettingPresenter(SettingPresenterListener settingPresenterListener) {
        this.settingPresenterListener = settingPresenterListener;
    }

    public void appStart() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            settingPresenterListener.openAuthActivity();
        } else {
            if (!firebaseUser.isEmailVerified()) {
                settingPresenterListener.openAuthActivity();
            } else {
                String displayName = firebaseUser.getDisplayName();
                Uri photoUrl = firebaseUser.getPhotoUrl();
                settingPresenterListener.initializePrintJob(displayName, photoUrl);
            }
        }
    }
}

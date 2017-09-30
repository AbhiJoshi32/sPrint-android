package com.binktec.sprint.presenter;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.binktec.sprint.interactor.presenter.SettingPresenterListener;
import com.binktec.sprint.modal.api.PrintApi;
import com.binktec.sprint.utility.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingPresenter {
    private SettingPresenterListener settingPresenterListener;
    FirebaseAuth firebaseAuth;

    public SettingPresenter(SettingPresenterListener settingPresenterListener) {
        this.settingPresenterListener = settingPresenterListener;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void appStart() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            settingPresenterListener.openAuthActivity();
        } else {
            if (!firebaseUser.isEmailVerified()) {
                settingPresenterListener.openAuthActivity();
            } else {
                settingPresenterListener.initializePrintJob();
            }
        }
    }

    public void updatePassword() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            firebaseAuth.sendPasswordResetEmail(user.getEmail())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                settingPresenterListener.emailSentSuccessful();
                            } else {
                                settingPresenterListener.emailSentUnsuccessful();
                            }
                        }
                    });
            }
        }

    public void logout() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        SessionManager.clearAllSession();
        settingPresenterListener.openAuthActivity();
    }

}

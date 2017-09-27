package com.binktec.sprint.presenter;

import android.support.annotation.NonNull;


import com.binktec.sprint.interactor.presenter.ManageAccountPresenterListener;
import com.binktec.sprint.modal.api.PrintApi;
import com.binktec.sprint.utility.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ManageAccountPresenter {
    private ManageAccountPresenterListener manageAccountPresenterListener;
    private FirebaseAuth firebaseAuth;

    public ManageAccountPresenter(ManageAccountPresenterListener manageAccountPresenterListener) {
        this.manageAccountPresenterListener = manageAccountPresenterListener;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void appStart() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            if (!firebaseUser.isEmailVerified()) {
                manageAccountPresenterListener.openAuthActivity();
            } else {
                if (firebaseUser.getPhotoUrl() != null) {
                    manageAccountPresenterListener.initializePrintJob(firebaseUser.getDisplayName(),
                            firebaseUser.getPhotoUrl().toString());
                } else {
                    manageAccountPresenterListener.initializePrintJob(firebaseUser.getDisplayName(),
                            "");
                }
            }
        } else {
            manageAccountPresenterListener.openAuthActivity();
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
                                    manageAccountPresenterListener.emailSentSuccessful();
                                } else {
                                    manageAccountPresenterListener.emailSentUnsuccessful();
                                }
                            }
                    });
        }
    }

    public void signOut() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            PrintApi printApi = new PrintApi(firebaseAuth.getCurrentUser().getUid());
            printApi.removeListeners();
        }
        firebaseAuth.signOut();
        SessionManager.clearAllSession();
        manageAccountPresenterListener.openAuthActivity();
    }
}

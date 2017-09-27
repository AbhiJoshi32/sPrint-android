package com.binktec.sprint.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.binktec.sprint.interactor.presenter.ManageAccountPresenterListener;
import com.binktec.sprint.modal.api.PrintApi;
import com.binktec.sprint.ui.fragment.ForgotPasswordFragment;
import com.binktec.sprint.utility.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class ManageAccountPresenter {
    private static final String TAG = "Mange aacoout";
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

    public void signOut() {
        firebaseAuth = FirebaseAuth.getInstance();
        PrintApi printApi = new PrintApi(firebaseAuth.getCurrentUser().getUid());
        printApi.removeListeners();
        firebaseAuth.signOut();
        SessionManager.clearAllSession();
        manageAccountPresenterListener.openAuthActivity();
    }
}

package com.binktec.sprint.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.binktec.sprint.interactor.presenter.ManageAccountPresenterListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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


    private void updatePassword(String newPassword) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });

    }
}

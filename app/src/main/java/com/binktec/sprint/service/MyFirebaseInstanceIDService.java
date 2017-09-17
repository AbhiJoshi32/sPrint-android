package com.binktec.sprint.service;


import android.util.Log;

import com.binktec.sprint.modal.api.UserApi;
import com.binktec.sprint.modal.pojo.User;
import com.binktec.sprint.utility.Misc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        if(mAuth.getCurrentUser() != null ){
            UserApi userApi = new UserApi();
            User user = new User();
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            Log.d(TAG, String.valueOf(firebaseUser.isEmailVerified()));
            user.setEmailId(firebaseUser.getEmail());
            user.setUid(firebaseUser.getUid());
            user.setRequestToken(token);
            user.setDateOfJoin(Misc.getDate());
            userApi.refreshToken(user);
        }
    }
}
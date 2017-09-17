package com.binktec.sprint.modal.api;

import android.util.Log;

import com.binktec.sprint.modal.pojo.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserApi {

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String TAG="User Api";

    public void refreshToken(User user) {
        Log.d(TAG,"user is syncing");
        DatabaseReference userref = database.getReference("Users").child(user.getUid());
        userref.setValue(user);
    }
}

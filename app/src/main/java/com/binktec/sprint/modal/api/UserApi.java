package com.binktec.sprint.modal.api;

import android.util.Log;

import com.binktec.sprint.interactor.modal.UserModalListener;
import com.binktec.sprint.modal.pojo.User;
import com.binktec.sprint.utility.Misc;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserApi {

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String TAG="User Api";

    private DatabaseReference userRef = database.getReference("Users");

    public void refreshToken(String token,String uid) {
        Log.d(TAG,"user is syncing");
        DatabaseReference userref = database.getReference("Users").child(uid).child("requestToken");
        userref.setValue(token);
    }

    public void syncUserWithBackEnd(final User user, final UserModalListener callback) {
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    User user = dataSnapshot.getValue(User.class);
                    callback.syncDatabaseValues(user);
                } else {
                    userRef.child(user.getUid()).setValue(user);
                    callback.initSessions();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void registerToBackend(User user) {
        userRef.child(user.getUid()).setValue(user);
    }
}

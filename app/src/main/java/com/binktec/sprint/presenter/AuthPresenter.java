package com.binktec.sprint.presenter;

import android.util.Log;

import com.binktec.sprint.interactor.presenter.AuthPresenterListener;
import com.binktec.sprint.modal.api.PrintApi;
import com.binktec.sprint.modal.api.UserApi;
import com.binktec.sprint.modal.pojo.User;
import com.binktec.sprint.utility.Constants;
import com.binktec.sprint.utility.Misc;
import com.binktec.sprint.utility.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AuthPresenter {

    private AuthPresenterListener authPresenterListener;
    private FirebaseAuth firebaseAuth;

    private static final Pattern p = Pattern.compile(Constants.vitRegex);
    private Matcher m;
    private static final String TAG = "Auth Presenter";

    public AuthPresenter(AuthPresenterListener authPresenterListener) {
        this.authPresenterListener = authPresenterListener;
        firebaseAuth = FirebaseAuth.getInstance();
    }



    public void validateRegister(String email, String password, String retypePassword) {
        m = p.matcher(email);
        if (email.isEmpty()) {
            authPresenterListener.invalidRegisterCred("email",Constants.emptyEmailError);
        } else if (password.isEmpty()) {
            authPresenterListener.invalidRegisterCred("password",Constants.emptyPassError);
        } else if (!m.matches()) {
            authPresenterListener.invalidRegisterCred("email",Constants.vitInvalid);
        } else if (password.length() < 6) {
            authPresenterListener.invalidRegisterCred("password",Constants.weakPassword);
        }else if (!password.equals(retypePassword)) {
            authPresenterListener.invalidRegisterCred("retypePassword",Constants.passNotMatch);
        }else {
            authPresenterListener.validRegisterCred(email,password);
        }
    }

    public void validateLogin(String email, String password) {
        m = p.matcher(email);
        if (email.isEmpty()) {
            authPresenterListener.invalidLoginCred("email",Constants.emptyEmailError);
        } else if (password.isEmpty()) {
            authPresenterListener.invalidLoginCred("password",Constants.emptyPassError);
        } else if (!m.matches()) {
            authPresenterListener.invalidLoginCred("email",Constants.vitInvalid);
        } else if (password.length() < 6) {
            authPresenterListener.invalidLoginCred("password",Constants.weakPassword);
        } else {
            authPresenterListener.validLoginCred(email,password);
        }
    }

    public void verifyGoogleSignInEmail(GoogleSignInAccount account) {
        String email = account.getEmail();
        if (email != null) {
            m = p.matcher(email);
            if (m.matches()) {
                authPresenterListener.validGoogleAcc(account);
            } else {
                authPresenterListener.invalidGoogleAcc();
            }
        }
        else {
            authPresenterListener.invalidGoogleAcc();
        }
    }

    public void syncUser() {
        Log.d(TAG,"sync user is callled");
        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        UserApi userApi = new UserApi();
        User user = new User();
        user.setDateOfJoin(Misc.getDate());
        user.setRequestToken(token);
        if (firebaseUser != null) {
            user.setEmailId(firebaseUser.getEmail());
            user.setUid(firebaseUser.getUid());
        }
        PrintApi printApi = new PrintApi(user.getUid());
        printApi.dummyEntry();
        SessionManager.saveUser(user);
        userApi.refreshToken(user);
    }
}

package com.binktec.sprint.interactor.presenter;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface AuthPresenterListener {

    void invalidRegisterCred(String errorElement, String error);

    void invalidLoginCred(String errorElement, String error);

    void validRegisterCred(String email, String password);

    void validLoginCred(String email, String password);

    void validGoogleAcc(GoogleSignInAccount account);

    void invalidGoogleAcc();
}

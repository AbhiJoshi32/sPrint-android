package com.binktec.sprint.interactor.fragment;


public interface AuthFragmentListener {
    void registerBtnClicked(String email, String password, String retypePassword);

    void signInTextClicked();

    void googleSignInClicked();

    void loginBtnClicked(String email, String password);

    void registerTextClicked();

    void resendVerBtnClicked();

    void logOutBtnClicked();
}

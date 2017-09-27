package com.binktec.sprint.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.AuthFragmentListener;
import com.google.android.gms.common.SignInButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginFragment extends Fragment {
    @BindView(R.id.login_input_password)
    EditText loginInputPassword;
    @BindView(R.id.login_input_email)
    EditText loginInputEmail;
    @BindView(R.id.loginProgessbar)
    ProgressBar loginProgessbar;
    Unbinder unbinder;
    @BindView(R.id.registerText)
    TextView registerText;
    @BindView(R.id.loginbutton)
    Button loginbutton;
    @BindView(R.id.google_button)
    SignInButton googleButton;

    private AuthFragmentListener authFragmentListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AuthFragmentListener) {
            authFragmentListener = (AuthFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AuthFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        googleButton.setSize(SignInButton.SIZE_WIDE);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.loginbutton, R.id.registerText, R.id.google_button, R.id.forgotPasswordTxt})
    public void onViewClicked(View view) {
        String loginEmail = loginInputEmail.getText().toString();
        String loginPassword = loginInputPassword.getText().toString();
        switch (view.getId()) {
            case R.id.loginbutton:
                authFragmentListener.loginBtnClicked(loginEmail, loginPassword);
                break;
            case R.id.registerText:
                authFragmentListener.registerTextClicked();
                break;
            case R.id.google_button:
                authFragmentListener.googleSignInClicked();
                break;
            case R.id.forgotPasswordTxt:
                authFragmentListener.forgotPassTxtClicked();
                break;
        }
    }

    public void showEmailError(String error) {
        loginInputEmail.setError(error);
        loginInputEmail.requestFocus();
    }

    public void showPasswordError(String error) {
        loginInputPassword.setError(error);
        loginInputPassword.requestFocus();
    }

    public void hideLoginFragProgressBar() {
        loginProgessbar.setVisibility(View.GONE);
        registerText.setEnabled(true);
        loginInputEmail.setEnabled(true);
        loginInputPassword.setEnabled(true);
        googleButton.setEnabled(true);
        loginbutton.setEnabled(true);
    }

    public void showLoginFragProgressBar() {
        registerText.setEnabled(false);
        loginbutton.setEnabled(false);
        googleButton.setEnabled(false);
        loginInputEmail.setEnabled(false);
        loginInputPassword.setEnabled(false);
        loginProgessbar.setVisibility(View.VISIBLE);
    }
}
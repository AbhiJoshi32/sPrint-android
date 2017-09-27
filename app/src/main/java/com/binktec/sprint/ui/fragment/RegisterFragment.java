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

public class RegisterFragment extends Fragment {
    @BindView(R.id.registerBtn)
    Button registerBtn;
    @BindView(R.id.register_input_password)
    EditText registerInputPassword;
    @BindView(R.id.register_input_retype_password)
    EditText registerInputRetypePassword;
    @BindView(R.id.register_input_email)
    EditText registerInputEmail;
    @BindView(R.id.regProgressbar)
    ProgressBar regProgressbar;
    Unbinder unbinder;
    @BindView(R.id.signInText)
    TextView signInText;
    @BindView(R.id.google_button)
    SignInButton googleButton;

    private AuthFragmentListener authFragmentListener;

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.registerBtn, R.id.signInText, R.id.google_button, R.id.regProgressbar})
    public void onViewClicked(View view) {
        String registerEmail = registerInputEmail.getText().toString();
        String registerPassword = registerInputPassword.getText().toString();
        String registerRetypePassword = registerInputRetypePassword.getText().toString();
        switch (view.getId()) {
            case R.id.registerBtn:
                authFragmentListener.registerBtnClicked(
                        registerEmail, registerPassword, registerRetypePassword);
                break;
            case R.id.signInText:
                authFragmentListener.signInTextClicked();
                break;
            case R.id.google_button:
                authFragmentListener.googleSignInClicked();
                break;
        }
    }

    public void registerShowEmailError(String error) {
        registerInputEmail.setError(error);
        registerInputEmail.requestFocus();
    }

    public void registerShowPasswordError(String error) {
        registerInputPassword.setError(error);
        registerInputPassword.requestFocus();
    }

    public void registerShowRetypePasswordError(String error) {
        registerInputRetypePassword.setError(error);
        registerInputRetypePassword.requestFocus();
    }

    public void hideRegFragProgressBar() {
        registerBtn.setEnabled(true);
        registerInputEmail.setEnabled(true);
        registerInputEmail.setEnabled(true);
        registerInputRetypePassword.setEnabled(true);
        googleButton.setEnabled(true);
        signInText.setEnabled(true);
        regProgressbar.setVisibility(View.INVISIBLE);
    }

    public void showRegFragProgressBar() {
        registerBtn.setEnabled(false);
        registerInputEmail.setEnabled(false);
        registerInputPassword.setEnabled(false);
        registerInputRetypePassword.setEnabled(false);
        googleButton.setEnabled(false);
        signInText.setEnabled(false);
        regProgressbar.setVisibility(View.VISIBLE);
    }
}
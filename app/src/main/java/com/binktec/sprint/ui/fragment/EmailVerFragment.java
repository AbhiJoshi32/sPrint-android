package com.binktec.sprint.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.AuthFragmentListener;
import com.binktec.sprint.utility.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EmailVerFragment extends Fragment {


    @BindView(R.id.resendButton)
    Button resendButton;

    Unbinder unbinder;
    @BindView(R.id.verifyText)
    TextView verifyText;
    private AuthFragmentListener authFragmentListener;

    public EmailVerFragment() {
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


    public static EmailVerFragment newInstance() {
        return new EmailVerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_ver, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void emailVerSendSuccessful() {
        verifyText.setText(Constants.sendVerificationSuccess);
    }

    public void emailVerSendUnsuccessful() {
        verifyText.setText(Constants.sendVerificationFailed);
    }

    @OnClick({R.id.resendButton, R.id.logoutButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.resendButton:
                authFragmentListener.resendVerBtnClicked();
                break;
            case R.id.logoutButton:
                authFragmentListener.logOutBtnClicked();
                break;
        }
    }
}

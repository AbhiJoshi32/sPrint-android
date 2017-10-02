package com.binktec.sprint.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.AuthFragmentListener;
import com.binktec.sprint.utility.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ForgotPasswordFragment extends Fragment {

    @BindView(R.id.enterEmailText)
    EditText enterEmailText;

    Unbinder unbinder;
    @BindView(R.id.linkText2)
    TextView linkText2;
    @BindView(R.id.forgot_pass_progress_bar)
    ProgressBar forgotPassProgressBar;
    private AuthFragmentListener authFragmentListener;


    public ForgotPasswordFragment() {
        // Required empty public constructor
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


    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        authFragmentListener = null;
    }

    @OnClick(R.id.forgot_button)
    public void onViewClicked() {
        Pattern p = Pattern.compile(Constants.vitRegex);
        Matcher m;
        String email = enterEmailText.getText().toString();
        m = p.matcher(email);
        if (email.isEmpty()) {
            enterEmailText.setError("Input email");
            enterEmailText.requestFocus();
        } else if (!m.matches()) {
            enterEmailText.setError("Only vit email allowed");
            enterEmailText.requestFocus();
        } else {
            authFragmentListener.forgotPassBtnClicked(email);
            forgotPassProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void showConfirmText() {
        linkText2.setText(getString(R.string.password_reset_msg));
        forgotPassProgressBar.setVisibility(View.GONE);
    }

    public void showSendError() {
        linkText2.setText(getString(R.string.password_reset_error));
        forgotPassProgressBar.setVisibility(View.GONE);
    }
}

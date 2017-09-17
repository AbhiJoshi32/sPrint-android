package com.binktec.sprint.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.ManageFragmentListener;
import com.binktec.sprint.utility.CircleTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class ManageAccountFragment extends Fragment {
    @BindView(R.id.imageView)
    ImageView imageView;
    Unbinder unbinder;
    private ManageFragmentListener manageFragmentListener;

    public ManageAccountFragment() {
        // Required empty public constructor
    }

    public static ManageAccountFragment newInstance() {
        return new ManageAccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_account, container, false);
        unbinder = ButterKnife.bind(this, view);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && firebaseUser.getPhotoUrl() != null) {
            Glide.with(this).load(firebaseUser.getPhotoUrl())
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ManageFragmentListener) {
            manageFragmentListener = (ManageFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MangeFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        manageFragmentListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.logoutButton)
    public void onViewClicked() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        manageFragmentListener.manageSignOut();
    }
}

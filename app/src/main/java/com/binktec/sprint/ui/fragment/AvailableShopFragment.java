package com.binktec.sprint.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.AvailShopFragmentListener;
import com.binktec.sprint.modal.pojo.shop.Shop;
import com.binktec.sprint.ui.adapter.AvailShopAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AvailableShopFragment extends Fragment {
    @BindView(R.id.avail_shop_recycler_view)
    RecyclerView availShopRecyclerView;
    @BindView(R.id.avail_shop_progessBar)
    ProgressBar availShopProgessBar;

    private AvailShopAdapter availShopAdapter;
    private List<Shop>availShops;

    private AvailShopFragmentListener availShopFragmentListener;
    Unbinder unbinder;

    public AvailableShopFragment() {
        // Required empty public constructor
    }

    public static AvailableShopFragment newInstance() {
        return new AvailableShopFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AvailShopFragmentListener) {
            availShopFragmentListener = (AvailShopFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AuthFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        availShopFragmentListener = null;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_shop, container, false);
        unbinder = ButterKnife.bind(this, view);
        availShops = new ArrayList<>();
        availShopAdapter = new AvailShopAdapter(availShops, new AvailShopAdapter.AvailShopAdapterListener(){
            @Override
            public void detailShop(View v, int position) {
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        availShopRecyclerView.setLayoutManager(mLayoutManager);
        availShopRecyclerView.setItemAnimator(new DefaultItemAnimator());
        availShopRecyclerView.setAdapter(availShopAdapter);
        availShopFragmentListener.getShops();
        return view;
    }

    public void updateShopRecyclerView(List<Shop> apiShops) {
        try {
            hideShopProgressBar();
            availShops.clear();
            availShops.addAll(apiShops);
            availShopAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideShopProgressBar() {
        availShopProgessBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

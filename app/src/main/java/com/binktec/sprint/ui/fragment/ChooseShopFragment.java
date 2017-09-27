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
import com.binktec.sprint.interactor.fragment.TransactionFragmentListener;
import com.binktec.sprint.modal.pojo.PrintTransaction;
import com.binktec.sprint.ui.adapter.ShopAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChooseShopFragment extends Fragment {
    @BindView(R.id.shop_recycler_view)
    RecyclerView shopRecyclerView;
    @BindView(R.id.shopProgessBar)
    ProgressBar shopProgessBar;

    private ShopAdapter shopAdapter;
    private List<PrintTransaction> printTransactions;
    private TransactionFragmentListener transactionFragmentListener;

    Unbinder unbinder;

    public ChooseShopFragment() {
    }

    public static ChooseShopFragment newInstance() {
        return new ChooseShopFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TransactionFragmentListener) {
            transactionFragmentListener = (TransactionFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AuthFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        transactionFragmentListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_shop, container, false);
        unbinder = ButterKnife.bind(this, view);
        printTransactions = new ArrayList<>();
        shopAdapter = new ShopAdapter(printTransactions, new ShopAdapter.ShopAdapterListener() {
            @Override
            public void confirmShop(View v, int position) {
                try {
                    transactionFragmentListener.shopCardClicked(printTransactions.get(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        shopRecyclerView.setLayoutManager(mLayoutManager);
        shopRecyclerView.setItemAnimator(new DefaultItemAnimator());
        shopRecyclerView.setAdapter(shopAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        transactionFragmentListener.initShopFragment();
    }

    public void updateShopRecyclerView(List<PrintTransaction> apiPrintTransactions) {
        if (apiPrintTransactions != null && shopAdapter != null) {
            hideShopProgressBar();
            printTransactions.clear();
            printTransactions.addAll(apiPrintTransactions);
            shopAdapter.notifyDataSetChanged();
        }
    }

    private void hideShopProgressBar() {
        shopProgessBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

package com.binktec.sprint.presenter;

import android.util.Log;

import com.binktec.sprint.interactor.modal.TransactionModalListener;
import com.binktec.sprint.interactor.presenter.AvailableShopPresenterListener;
import com.binktec.sprint.modal.api.PrintApi;
import com.binktec.sprint.modal.pojo.shop.Shop;
import com.binktec.sprint.utility.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AvailableShopPresenter implements TransactionModalListener{

    private static final String TAG = "'Availavle shop";
    private AvailableShopPresenterListener availShopPreseneterListener;
    private PrintApi printApi;
    private FirebaseAuth firebaseAuth;

    public AvailableShopPresenter(AvailableShopPresenterListener availShopPreseneterListener) {
        this.availShopPreseneterListener = availShopPreseneterListener;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void appStart() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        printApi = new PrintApi();
        if (firebaseUser != null) {
            if (firebaseUser.getPhotoUrl() != null) {
                availShopPreseneterListener.initTransactionActivity(firebaseUser.getDisplayName(),
                        firebaseUser.getPhotoUrl().toString());
            } else {
                availShopPreseneterListener.initTransactionActivity(firebaseUser.getDisplayName(),
                        "");
            }
        }
    }

    public void retrieveShopList() {
        Log.d(TAG,"retrieving data ");
        if (SessionManager.getShops() != null) {
            availShopPreseneterListener.updateShopList(SessionManager.getShops());
        }
        printApi.getShopInfoApi(this);
    }

    @Override
    public void apiShopRetrievalSuccessful(List<Shop> apiShops) {
        SessionManager.saveShopList(apiShops);
        availShopPreseneterListener.updateShopList(apiShops);
    }

    @Override
    public void apiShopRetrievalUnsuccessful(String s) {

    }
}

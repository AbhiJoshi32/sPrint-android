package com.binktec.sprint.presenter;

import com.binktec.sprint.interactor.modal.TransactionModalListener;
import com.binktec.sprint.interactor.presenter.AvailableShopPresenterListener;
import com.binktec.sprint.modal.api.PrintApi;
import com.binktec.sprint.modal.pojo.shop.Shop;
import com.binktec.sprint.utility.SessionManager;

import java.util.List;

public class AvailableShopPresenter implements TransactionModalListener{

    private AvailableShopPresenterListener availShopPreseneterListener;
    private PrintApi printApi;

    public AvailableShopPresenter(AvailableShopPresenterListener availShopPreseneterListener) {
        this.availShopPreseneterListener = availShopPreseneterListener;
        printApi = new PrintApi(SessionManager.getUser().getUid());
    }

    public void appStart() {
        availShopPreseneterListener.initTransactionActivity();
    }

    public void retrieveShopList() {
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
        availShopPreseneterListener.showToast();
    }
}

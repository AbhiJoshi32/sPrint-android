package com.binktec.sprint.interactor.presenter;

import com.binktec.sprint.modal.pojo.shop.Shop;

import java.util.List;

public interface AvailableShopPresenterListener {
    void updateShopList(List<Shop> apiShops);

    void initTransactionActivity();
    void showToast();
}

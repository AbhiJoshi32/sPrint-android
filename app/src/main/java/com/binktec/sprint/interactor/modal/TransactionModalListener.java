package com.binktec.sprint.interactor.modal;

import com.binktec.sprint.modal.pojo.shop.Shop;

import java.util.List;

public interface TransactionModalListener {
    void apiShopRetrievalSuccessful(List<Shop> apiShops);

    void apiShopRetrievalUnsuccessful(String s);

}

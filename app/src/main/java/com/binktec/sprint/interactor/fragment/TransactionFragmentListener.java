package com.binktec.sprint.interactor.fragment;

import com.binktec.sprint.modal.pojo.PrintTransaction;

public interface TransactionFragmentListener {
    void removeFile(int position);

    void shopCardClicked(PrintTransaction printTransaction);

    void initShopFragment();

}

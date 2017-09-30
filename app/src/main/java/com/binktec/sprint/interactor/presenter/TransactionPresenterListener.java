package com.binktec.sprint.interactor.presenter;

import com.binktec.sprint.modal.pojo.FileDetail;
import com.binktec.sprint.modal.pojo.PrintTransaction;

import java.util.List;

public interface TransactionPresenterListener {
    void initTransactionActivity();

    void updateChooseFileFragment(List<FileDetail> chosenFiles);

    void showPrintDetailError(String s);

    void showFileError(String empty);

    void updatePrintDetails(int size);

    void proceedToShopSelection();

    void updateShopFragment(List<PrintTransaction> printTransactions);

    void openPrintJobActivity();

    void disableDone();

    void enableDone();

    void ongoingUpload();

    void FileError();

    void openAuthActivity();
}
